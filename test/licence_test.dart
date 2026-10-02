import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:getnet_payment_tech/getnet_payment_tech.dart';

class _FakeHandler implements IGetnetHandler {
  @override
  Future<void> onChanged(String message) async {}
  @override
  Future<void> onError(String message) async {}
  @override
  Future<void> onFinishedResponse(String message) async {}
  @override
  Future<void> onLoading(bool show) async {}
  @override
  Future<void> onMessage(String message) async {}
  @override
  Future<void> onTransactionSuccess() async {}
}

const _testLicenceKey = 'JYL-TEST-0000-0000';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const channel = MethodChannel(GetnetTech.channelName);
  final calls = <MethodCall>[];
  final messenger =
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger;

  /// Nativo fake: initPayment responde conforme [licenceValid]; as demais
  /// operações respondem LICENCE_ERROR se [operationLicenceValid] for false.
  void mockNative({
    bool licenceValid = true,
    bool operationLicenceValid = true,
  }) {
    messenger.setMockMethodCallHandler(channel, (call) async {
      calls.add(call);
      if (call.method == 'initPayment') {
        if (!licenceValid) {
          throw PlatformException(
            code: 'LICENCE_ERROR',
            message: 'Sua licença não foi ativada. Licença vencida',
          );
        }
        return true;
      }
      if (!operationLicenceValid) {
        throw PlatformException(
          code: 'LICENCE_ERROR',
          message: 'Sua licença não foi ativada. Licença cancelada.',
        );
      }
      return {'code': 'SUCCESS'};
    });
  }

  setUp(() {
    calls.clear();
    Payment.resetForTesting();
  });

  tearDown(() {
    messenger.setMockMethodCallHandler(channel, null);
  });

  test('licenceCheck aponta para o servidor oficial (core)', () {
    final url = utf8.decode(base64.decode(licenceCheck));
    expect(
      url,
      'https://portal-api.jylabtech.com.br/pos-payments/licence/check',
    );
    final uri = Uri.parse(url);
    expect(uri.scheme, 'https');
    expect(uri.host, 'portal-api.jylabtech.com.br');
    expect(uri.path, startsWith('/pos-payments/'));
  });

  test(
    'initPayment envia licenceKey e licenceInternalKey padrão do core',
    () async {
      mockNative();
      await GetnetTech(
        channel,
      ).initPayment(handler: _FakeHandler(), licenceKey: _testLicenceKey);

      final init = calls.singleWhere((c) => c.method == 'initPayment');
      expect(init.arguments['licenceKey'], _testLicenceKey);
      expect(init.arguments['licenceInternalKey'], licenceCheck);
    },
  );

  test('initPayment repassa licenceInternalKey informado', () async {
    mockNative();
    const custom =
        'https://portal-api.jylabtech.com.br/pos-payments/licence/check/invoice';
    await GetnetTech(channel).initPayment(
      handler: _FakeHandler(),
      licenceKey: _testLicenceKey,
      licenceInternalKey: custom,
    );

    final init = calls.singleWhere((c) => c.method == 'initPayment');
    expect(init.arguments['licenceInternalKey'], custom);
  });

  test('initPayment lança LICENCE_ERROR quando a licença é inválida', () async {
    mockNative(licenceValid: false);
    await expectLater(
      GetnetTech(
        channel,
      ).initPayment(handler: _FakeHandler(), licenceKey: _testLicenceKey),
      throwsA(
        isA<PlatformException>().having((e) => e.code, 'code', 'LICENCE_ERROR'),
      ),
    );
  });

  test('payment antes do initPayment falha', () {
    expect(() => GetnetTech(channel).payment, throwsA(isA<String>()));
  });

  test(
    'operações retornam false quando o nativo responde LICENCE_ERROR',
    () async {
      mockNative(operationLicenceValid: false);
      final payment = await GetnetTech(
        channel,
      ).initPayment(handler: _FakeHandler(), licenceKey: _testLicenceKey);

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
          transactionDate: '01/10/2026',
          cvNumber: '123',
        ),
        isFalse,
      );
      expect(
        await payment.preAuthorization(
          amount: 10,
          callerId: 'c1',
          orderId: 'o1',
        ),
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
    },
  );

  test(
    'após falha de licença, nova inicialização revalida no nativo',
    () async {
      final tech = GetnetTech(channel);
      mockNative(licenceValid: false);
      await expectLater(
        tech.initPayment(handler: _FakeHandler(), licenceKey: _testLicenceKey),
        throwsA(isA<PlatformException>()),
      );
      // O app não recebe um Payment utilizável.
      expect(() => tech.payment, throwsA(isA<String>()));

      calls.clear();
      mockNative();
      final payment = await tech.initPayment(
        handler: _FakeHandler(),
        licenceKey: _testLicenceKey,
      );
      expect(calls.where((c) => c.method == 'initPayment'), hasLength(1));
      expect(tech.payment, same(payment));
    },
  );

  test('operação com licença válida chama o nativo', () async {
    mockNative();
    final payment = await GetnetTech(
      channel,
    ).initPayment(handler: _FakeHandler(), licenceKey: _testLicenceKey);
    expect(
      await payment.creditPayment(amount: 1.5, callerId: 'c1', orderId: 'o1'),
      isTrue,
    );
    expect(calls.map((c) => c.method), contains('payment'));
  });

  test('operação cancelada no app da Getnet completa o Future com false', () async {
    messenger.setMockMethodCallHandler(channel, (call) async {
      calls.add(call);
      if (call.method == 'initPayment') return true;
      throw PlatformException(
        code: 'CANCELED',
        message:
            'Operação cancelada ou não concluída no app da Getnet (resultCode=0).',
      );
    });
    final payment = await GetnetTech(
      channel,
    ).initPayment(handler: _FakeHandler(), licenceKey: _testLicenceKey);

    expect(
      await payment.creditPayment(amount: 1.5, callerId: 'c1', orderId: 'o1'),
      isFalse,
    );
    expect(await payment.paymentStatus(callerId: 'c1'), isFalse);
  });
}
