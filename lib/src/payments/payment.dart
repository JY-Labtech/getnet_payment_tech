import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:getnet_payment_tech/src/interface/handler/igetnet_handler.dart';
import 'package:getnet_payment_tech/src/interface/model/getnet_printer.dart';
import 'package:getnet_payment_tech/src/interface/model/getnet_transaction_model.dart';
import 'package:getnet_payment_tech/src/interface/utils/extensions/payment_type_call_ext.dart';
import 'package:getnet_payment_tech/src/interface/utils/types/payment_type_call.dart';

import 'helper/igetnet_helper.dart';

final class Payment {
  final MethodChannel channel;
  IGetnetHandler paymentHandler;
  bool _nativeInitialised;

  // Cache da instância criada
  static Payment? _instance;

  Payment._({required this.channel, required this.paymentHandler})
    : _nativeInitialised = false;

  /// Método factory assíncrono para criação controlada
  static Future<Payment> create({
    required MethodChannel channel,
    required IGetnetHandler paymentHandler,
  }) async {
    // Se já existe instância, reutiliza
    if (_instance != null) {
      await _instance!.ensureInitialised(handler: paymentHandler);
      return _instance!;
    }

    final payment = Payment._(channel: channel, paymentHandler: paymentHandler);
    _instance = payment;

    payment._registerHandler();
    await payment._initialiseNative();

    return payment;
  }

  /// Limpa a instância em cache. Uso exclusivo em testes.
  @visibleForTesting
  static void resetForTesting() {
    _instance = null;
  }

  static void _handleTransaction(GetnetTransactionModel model) {
    // Handle transaction if needed
  }

  /// Troca o handler e garante que o nativo foi inicializado.
  Future<void> ensureInitialised({required IGetnetHandler handler}) async {
    paymentHandler = handler;
    _registerHandler();
    await _ensureNativeInitialised();
  }

  void setHandler(IGetnetHandler handler) {
    if (_instance == null) {
      throw Exception(
        'Payment instance not initialized. Call Payment.create() first.',
      );
    }

    _instance?.paymentHandler = handler;
    _instance?._registerHandler();
  }

  /// Function to invoke method from credit payment
  Future<bool> creditPayment({
    required double amount,
    required String callerId,
    required String orderId,
    bool allowPrintCurrentTransaction = false,
  }) async {
    try {
      await _ensureNativeInitialised();
      final result = await channel
          .invokeMethod(PaymentTypeCall.payment.method, {
            "amount": (amount * 100).round().toString().padLeft(12, '0'),
            "currencyPosition": "CURRENCY_BEFORE_AMOUNT",
            "currencyCode": 986,
            "paymentType": "CREDIT",
            "callerId": callerId,
            "orderId": orderId,
            "allowPrintCurrentTransaction": allowPrintCurrentTransaction,
            "creditType": "",
            "installments": 1,
          });
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from credit installment payment
  Future<bool> creditPaymentParc({
    required double amount,
    required String callerId,
    required String orderId,
    required int installments,
    bool allowPrintCurrentTransaction = false,
  }) async {
    try {
      await _ensureNativeInitialised();
      final result = await channel
          .invokeMethod(PaymentTypeCall.paymentCreditParc.method, {
            "amount": (amount * 100).round().toString().padLeft(12, '0'),
            "currencyPosition": "CURRENCY_BEFORE_AMOUNT",
            "currencyCode": 986,
            "paymentType": "CREDIT",
            "callerId": callerId,
            "orderId": orderId,
            "allowPrintCurrentTransaction": allowPrintCurrentTransaction,
            "creditType": "A_VISTA",
            "installments": installments,
          });
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from debit payment
  Future<bool> debitPayment({
    required double amount,
    required String callerId,
    required String orderId,
    bool allowPrintCurrentTransaction = false,
  }) async {
    try {
      await _ensureNativeInitialised();
      final result = await channel
          .invokeMethod(PaymentTypeCall.payment.method, {
            "amount": (amount * 100).round().toString().padLeft(12, '0'),
            "currencyPosition": "CURRENCY_BEFORE_AMOUNT",
            "currencyCode": 986,
            "paymentType": "DEBIT",
            "callerId": callerId,
            "orderId": orderId,
            "allowPrintCurrentTransaction": allowPrintCurrentTransaction,
            "creditType": "",
            "installments": 1,
          });
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from voucher payment
  Future<bool> voucherPayment({
    required double amount,
    required String callerId,
    required String orderId,
    bool allowPrintCurrentTransaction = false,
  }) async {
    try {
      await _ensureNativeInitialised();
      final result = await channel
          .invokeMethod(PaymentTypeCall.payment.method, {
            "amount": (amount * 100).round().toString().padLeft(12, '0'),
            "currencyPosition": "CURRENCY_BEFORE_AMOUNT",
            "currencyCode": 986,
            "paymentType": "VOUCHER",
            "callerId": callerId,
            "orderId": orderId,
            "allowPrintCurrentTransaction": allowPrintCurrentTransaction,
            "creditType": "",
            "installments": 1,
          });
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from pix payment
  Future<bool> pixPayment({
    required double amount,
    required String callerId,
    required String orderId,
    bool allowPrintCurrentTransaction = false,
  }) async {
    try {
      await _ensureNativeInitialised();
      final result = await channel
          .invokeMethod(PaymentTypeCall.payment.method, {
            "amount": (amount * 100).round().toString().padLeft(12, '0'),
            "currencyPosition": "CURRENCY_BEFORE_AMOUNT",
            "currencyCode": 986,
            "paymentType": "PIX",
            "callerId": callerId,
            "orderId": orderId,
            "allowPrintCurrentTransaction": allowPrintCurrentTransaction,
            "creditType": "",
            "installments": 1,
          });
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from refund payment
  Future<bool> refundPayment({
    required double amount,
    required String callerId,
    required String transactionDate,
    required String cvNumber,
    String? originTerminal,
    bool allowPrintCurrentTransaction = false,
  }) async {
    try {
      await _ensureNativeInitialised();
      final result = await channel
          .invokeMethod(PaymentTypeCall.paymentRefund.method, {
            "amount": (amount * 100).round().toString().padLeft(12, '0'),
            "callerId": callerId,
            "transactionDate": transactionDate,
            "cvNumber": cvNumber,
            "originTerminal": originTerminal ?? "",
            "allowPrintCurrentTransaction": allowPrintCurrentTransaction,
          });
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from pre-authorization
  Future<bool> preAuthorization({
    required double amount,
    required String callerId,
    required String orderId,
    bool allowPrintCurrentTransaction = false,
  }) async {
    try {
      await _ensureNativeInitialised();
      final result = await channel
          .invokeMethod(PaymentTypeCall.preAuthorization.method, {
            "amount": (amount * 100).round().toString().padLeft(12, '0'),
            "currencyPosition": "CURRENCY_BEFORE_AMOUNT",
            "currencyCode": 986,
            "callerId": callerId,
            "orderId": orderId,
            "allowPrintCurrentTransaction": allowPrintCurrentTransaction,
          });
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from payment status
  Future<bool> paymentStatus({
    required String callerId,
    bool allowPrintCurrentTransaction = false,
  }) async {
    try {
      await _ensureNativeInitialised();
      final result = await channel
          .invokeMethod(PaymentTypeCall.paymentStatus.method, {
            "callerId": callerId,
            "allowPrintCurrentTransaction": allowPrintCurrentTransaction,
          });
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from print
  Future<bool> print({required GetnetPrinterParams printerParams}) async {
    try {
      await _ensureNativeInitialised();
      await channel.invokeMethod(
        PaymentTypeCall.print.method,
        printerParams.toMap(),
      );
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from reprint
  Future<bool> reprint() async {
    try {
      await _ensureNativeInitialised();
      await channel.invokeMethod(PaymentTypeCall.reprint.method);
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Function to get device info
  Future<bool> getDeviceInfo() async {
    try {
      await _ensureNativeInitialised();
      final result = await channel.invokeMethod(
        PaymentTypeCall.deviceInfo.method,
      );
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Lê os dados do aparelho no nativo e os devolve.
  ///
  /// Retorna um mapa com `deviceModel` (`Build.MODEL`) e `serialNumber`
  /// (no Android 8.0+ o sistema não expõe o serial e o valor é
  /// `"Block Info"`), ou `null` em caso de erro.
  Future<Map<String, dynamic>?> getDeviceInfoData() async {
    try {
      await _ensureNativeInitialised();
      final result = await channel.invokeMethod<Map<dynamic, dynamic>>(
        PaymentTypeCall.deviceInfo.method,
      );
      final data = result?['data'];
      if (data is! Map) return null;
      return data.map((key, value) => MapEntry(key.toString(), value));
    } catch (e) {
      return null;
    }
  }

  /// Function to get info
  Future<bool> getInfo() async {
    try {
      await _ensureNativeInitialised();
      final result = await channel.invokeMethod(PaymentTypeCall.info.method);
      return result != null;
    } catch (e) {
      return false;
    }
  }

  void _registerHandler() {
    channel.setMethodCallHandler(
      (call) => IGetnetHelper.callHandler(
        call: call,
        iGetnetHandler: paymentHandler,
        onTransaction: (model) => _handleTransaction(model),
      ),
    );
  }

  Future<void> _initialiseNative() async {
    await channel.invokeMethod('initPayment');
    _nativeInitialised = true;
  }

  Future<void> _ensureNativeInitialised() async {
    if (!_nativeInitialised) {
      await _initialiseNative();
    }
  }
}
