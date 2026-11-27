import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:getnet_payment_tech/getnet_payment_tech.dart';

class _FakeHandler implements IGetnetHandler {
  final events = <String>[];

  @override
  Future<void> onChanged(String message) async =>
      events.add('changed:$message');
  @override
  Future<void> onError(String message) async => events.add('error:$message');
  @override
  Future<void> onFinishedResponse(String message) async =>
      events.add('finished:$message');
  @override
  Future<void> onLoading(bool show) async => events.add('loading:$show');
  @override
  Future<void> onMessage(String message) async =>
      events.add('message:$message');
  @override
  Future<void> onTransactionSuccess() async => events.add('success');
}

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const channel = MethodChannel(GetnetTech.channelName);
  final calls = <MethodCall>[];
  final messenger =
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger;

  /// Nativo fake: responde SUCCESS; com [failWith] lança PlatformException
  /// nas operações (o initPayment sempre responde true).
  void mockNative({String? failWith}) {
    messenger.setMockMethodCallHandler(channel, (call) async {
      calls.add(call);
      if (call.method == 'initPayment') return true;
      if (failWith != null) {
        throw PlatformException(code: failWith, message: 'falha $failWith');
      }
      return {'code': 'SUCCESS'};
    });
  }

  Map<dynamic, dynamic> argsOf(String method) =>
      calls.singleWhere((c) => c.method == method).arguments as Map;

  setUp(() {
    calls.clear();
    Payment.resetForTesting();
  });

  tearDown(() {
    messenger.setMockMethodCallHandler(channel, null);
  });

  group('initPayment', () {
    test('chama o nativo sem argumentos', () async {
      mockNative();
      await GetnetTech(channel).initPayment(handler: _FakeHandler());

      final init = calls.singleWhere((c) => c.method == 'initPayment');
      expect(init.arguments, isNull);
    });

    test('payment antes do initPayment falha', () {
      expect(() => GetnetTech(channel).payment, throwsA(isA<String>()));
    });

    test('segunda chamada só troca o handler', () async {
      mockNative();
      final tech = GetnetTech(channel);
      final first = await tech.initPayment(handler: _FakeHandler());
      final handler = _FakeHandler();
      final second = await tech.initPayment(handler: handler);

      expect(second, same(first));
      expect(second.paymentHandler, same(handler));
      expect(calls.where((c) => c.method == 'initPayment'), hasLength(1));
    });

    test(
      'NATIVE_UNAVAILABLE é propagado e a próxima chamada tenta de novo',
      () async {
        final tech = GetnetTech(channel);
        messenger.setMockMethodCallHandler(channel, (call) async {
          calls.add(call);
          throw PlatformException(
            code: 'NATIVE_UNAVAILABLE',
            message: 'sem SDK',
          );
        });
        await expectLater(
          tech.initPayment(handler: _FakeHandler()),
          throwsA(
            isA<PlatformException>().having(
              (e) => e.code,
              'code',
              'NATIVE_UNAVAILABLE',
            ),
          ),
        );
        expect(() => tech.payment, throwsA(isA<String>()));

        calls.clear();
        mockNative();
        final payment = await tech.initPayment(handler: _FakeHandler());
        expect(calls.where((c) => c.method == 'initPayment'), hasLength(1));
        expect(tech.payment, same(payment));
      },
    );
  });

  group('operações chamam o nativo direto', () {
    late Payment payment;

    setUp(() async {
      mockNative();
      payment = await GetnetTech(channel).initPayment(handler: _FakeHandler());
      calls.clear();
    });

    test('creditPayment', () async {
      expect(
        await payment.creditPayment(
          amount: 10.5,
          callerId: 'c1',
          orderId: 'o1',
        ),
        isTrue,
      );
      final args = argsOf('payment');
      expect(args['amount'], '000000001050');
      expect(args['paymentType'], 'CREDIT');
      expect(args['currencyCode'], 986);
      expect(args['currencyPosition'], 'CURRENCY_BEFORE_AMOUNT');
      expect(args['callerId'], 'c1');
      expect(args['orderId'], 'o1');
      expect(args['installments'], 1);
      expect(args['creditType'], '');
      expect(args['allowPrintCurrentTransaction'], isFalse);
    });

    test('creditPaymentParc', () async {
      expect(
        await payment.creditPaymentParc(
          amount: 100,
          callerId: 'c1',
          orderId: 'o1',
          installments: 3,
          allowPrintCurrentTransaction: true,
        ),
        isTrue,
      );
      final args = argsOf('paymentCreditParc');
      expect(args['amount'], '000000010000');
      expect(args['paymentType'], 'CREDIT');
      expect(args['installments'], 3);
      expect(args['allowPrintCurrentTransaction'], isTrue);
    });

    test('debitPayment, voucherPayment e pixPayment', () async {
      expect(
        await payment.debitPayment(amount: 1, callerId: 'c1', orderId: 'o1'),
        isTrue,
      );
      expect(
        await payment.voucherPayment(amount: 1, callerId: 'c1', orderId: 'o1'),
        isTrue,
      );
      expect(
        await payment.pixPayment(amount: 1, callerId: 'c1', orderId: 'o1'),
        isTrue,
      );
      expect(calls.map((c) => (c.arguments as Map)['paymentType']), [
        'DEBIT',
        'VOUCHER',
        'PIX',
      ]);
      expect(calls.map((c) => c.method), everyElement('payment'));
    });

    test('refundPayment', () async {
      expect(
        await payment.refundPayment(
          amount: 2,
          callerId: 'c1',
          transactionDate: '25122026',
          cvNumber: '123',
        ),
        isTrue,
      );
      final args = argsOf('refundPayment');
      expect(args['amount'], '000000000200');
      expect(args['transactionDate'], '25122026');
      expect(args['cvNumber'], '123');
      expect(args['originTerminal'], '');
    });

    test('preAuthorization', () async {
      expect(
        await payment.preAuthorization(
          amount: 3,
          callerId: 'c1',
          orderId: 'o1',
        ),
        isTrue,
      );
      final args = argsOf('preAuthorization');
      expect(args['amount'], '000000000300');
      expect(args['orderId'], 'o1');
    });

    test('paymentStatus', () async {
      expect(await payment.paymentStatus(callerId: 'c1'), isTrue);
      expect(argsOf('statusPayment')['callerId'], 'c1');
    });

    test('reprint, getInfo e getDeviceInfo', () async {
      expect(await payment.reprint(), isTrue);
      expect(await payment.getInfo(), isTrue);
      expect(await payment.getDeviceInfo(), isTrue);
      expect(calls.map((c) => c.method), ['reprint', 'info', 'deviceInfo']);
    });

    test('print envia printable_content', () async {
      expect(
        await payment.print(
          printerParams: GetnetPrinterParams(
            text: 'Olá',
            base64Image: 'aW1n',
            lines: 2,
          ),
        ),
        isTrue,
      );
      final content = argsOf('print')['printable_content'] as List;
      expect(content, [
        {'type': 'text', 'content': 'Olá', 'align': 'left', 'size': 'medium'},
        {'type': 'image', 'imagePath': 'aW1n'},
        {'type': 'text', 'content': '\n\n', 'align': 'left', 'size': 'medium'},
      ]);
    });
  });

  group('getDeviceInfoData', () {
    setUp(() {
      messenger.setMockMethodCallHandler(channel, (call) async {
        calls.add(call);
        if (call.method == 'initPayment') return true;
        if (call.method == 'deviceInfo') {
          return {
            'code': 'SUCCESS',
            'data': {'deviceModel': 'GPOS700', 'serialNumber': 'Block Info'},
          };
        }
        return null;
      });
    });

    test('devolve os dados do aparelho', () async {
      final payment = await GetnetTech(
        channel,
      ).initPayment(handler: _FakeHandler());
      expect(await payment.getDeviceInfoData(), {
        'deviceModel': 'GPOS700',
        'serialNumber': 'Block Info',
      });
      expect(await payment.getDeviceInfo(), isTrue);
    });

    test('erro do nativo devolve null', () async {
      final payment = await GetnetTech(
        channel,
      ).initPayment(handler: _FakeHandler());
      messenger.setMockMethodCallHandler(channel, (call) async {
        throw PlatformException(code: 'UNAVAILABLE');
      });
      expect(await payment.getDeviceInfoData(), isNull);
    });
  });

  test('operações retornam false quando o nativo responde erro', () async {
    mockNative(failWith: 'CANCELED');
    final payment = await GetnetTech(
      channel,
    ).initPayment(handler: _FakeHandler());

    expect(
      await payment.creditPayment(amount: 10, callerId: 'c1', orderId: 'o1'),
      isFalse,
    );
    expect(
      await payment.creditPaymentParc(
        amount: 10,
        callerId: 'c1',
        orderId: 'o1',
        installments: 2,
      ),
      isFalse,
    );
    expect(
      await payment.debitPayment(amount: 10, callerId: 'c1', orderId: 'o1'),
      isFalse,
    );
    expect(
      await payment.voucherPayment(amount: 10, callerId: 'c1', orderId: 'o1'),
      isFalse,
    );
    expect(
      await payment.pixPayment(amount: 10, callerId: 'c1', orderId: 'o1'),
      isFalse,
    );
    expect(
      await payment.refundPayment(
        amount: 10,
        callerId: 'c1',
        transactionDate: '01102026',
        cvNumber: '123',
      ),
      isFalse,
    );
    expect(
      await payment.preAuthorization(amount: 10, callerId: 'c1', orderId: 'o1'),
      isFalse,
    );
    expect(await payment.paymentStatus(callerId: 'c1'), isFalse);
    expect(await payment.reprint(), isFalse);
    expect(await payment.getInfo(), isFalse);
    expect(await payment.getDeviceInfo(), isFalse);
    expect(
      await payment.print(printerParams: GetnetPrinterParams(text: 'teste')),
      isFalse,
    );
  });

  group('handler', () {
    Future<void> nativeCalls(String method, dynamic arguments) async {
      final data = const StandardMethodCodec().encodeMethodCall(
        MethodCall(method, arguments),
      );
      await messenger.handlePlatformMessage(
        GetnetTech.channelName,
        data,
        (_) {},
      );
    }

    test('eventos do nativo chegam ao handler', () async {
      mockNative();
      final handler = _FakeHandler();
      await GetnetTech(channel).initPayment(handler: handler);

      final response = jsonEncode({
        'code': 'SUCCESS',
        'data': {'result': '0', 'nsu': '42'},
      });
      await nativeCalls('onFinishedResponse', response);
      await nativeCalls('onError', 'Negada');
      await nativeCalls('onMessage', 'Aproxime o cartão');
      await nativeCalls('onChanged', 'x');
      await nativeCalls('onLoading', true);
      await nativeCalls('onTransactionSuccess', null);

      expect(handler.events, [
        'finished:$response',
        'error:Negada',
        'message:Aproxime o cartão',
        'changed:x',
        'loading:true',
        'success',
      ]);
    });
  });

  group('modelos', () {
    test('GetnetTransactionModel.fromMap converte amount em centavos', () {
      final model = GetnetTransactionModel.fromMap({
        'result': 0,
        'amount': '000000001050',
        'nsu': 123,
      });
      expect(model.isApproved, isTrue);
      expect(model.amount, 10.5);
      expect(model.nsu, '123');
    });

    test('GetnetTransactionModel.toError', () {
      final model = GetnetTransactionModel.toError('falhou');
      expect(model.isApproved, isFalse);
      expect(model.errorMessage, 'falhou');
    });

    test('GetnetResponse.fromJson', () {
      final r = GetnetResponse.fromJson('{"method":"payment","result":0}');
      expect(r.method, 'payment');
      expect(r.result, 0);
    });

    test('GetnetPrinterParams sem conteúdo gera lista vazia', () {
      expect(GetnetPrinterParams().printableContent, isEmpty);
    });
  });
}
