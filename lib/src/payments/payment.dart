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

  // Cache da instância criada
  static Payment? _instance;

  Payment._({required this.channel, required this.paymentHandler});

  /// Método factory assíncrono para criação controlada
  static Payment create({required MethodChannel channel, required IGetnetHandler paymentHandler}) {
    // Se já existe instância, retorna ela diretamente
    if (_instance != null) {
      return _instance!;
    }

    // Cria nova instância
    final payment = Payment._(channel: channel, paymentHandler: paymentHandler);

    // Salva instância
    _instance = payment;

    payment.channel.setMethodCallHandler((e) => IGetnetHelper.callHandler(call: e, iGetnetHandler: paymentHandler, onTransaction: (model) => _handleTransaction(model)));

    return payment;
  }

  static void _handleTransaction(GetnetTransactionModel model) {
    // Handle transaction if needed
  }

  void setHandler(IGetnetHandler handler) {
    if (_instance == null) {
      throw Exception('Payment instance not initialized. Call Payment.create() first.');
    }

    _instance?.paymentHandler = handler;
    _instance?.channel.setMethodCallHandler((e) => IGetnetHelper.callHandler(call: e, iGetnetHandler: paymentHandler, onTransaction: (model) => _handleTransaction(model)));
  }

  /// Function to invoke method from credit payment
  Future<bool> creditPayment({required double amount, required String callerId, required String orderId, bool allowPrintCurrentTransaction = false}) async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.payment.method, {"amount": (amount * 100).round().toString().padLeft(12, '0'), "currencyPosition": "CURRENCY_BEFORE_AMOUNT", "currencyCode": 986, "paymentType": "CREDIT", "callerId": callerId, "orderId": orderId, "allowPrintCurrentTransaction": allowPrintCurrentTransaction, "creditType": "", "installments": 1});
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from credit installment payment
  Future<bool> creditPaymentParc({required double amount, required String callerId, required String orderId, required int installments, bool allowPrintCurrentTransaction = false}) async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.paymentCreditParc.method, {"amount": (amount * 100).round().toString().padLeft(12, '0'), "currencyPosition": "CURRENCY_BEFORE_AMOUNT", "currencyCode": 986, "paymentType": "CREDIT", "callerId": callerId, "orderId": orderId, "allowPrintCurrentTransaction": allowPrintCurrentTransaction, "creditType": "A_VISTA", "installments": installments});
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from debit payment
  Future<bool> debitPayment({required double amount, required String callerId, required String orderId, bool allowPrintCurrentTransaction = false}) async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.payment.method, {"amount": (amount * 100).round().toString().padLeft(12, '0'), "currencyPosition": "CURRENCY_BEFORE_AMOUNT", "currencyCode": 986, "paymentType": "DEBIT", "callerId": callerId, "orderId": orderId, "allowPrintCurrentTransaction": allowPrintCurrentTransaction, "creditType": "", "installments": 1});
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from voucher payment
  Future<bool> voucherPayment({required double amount, required String callerId, required String orderId, bool allowPrintCurrentTransaction = false}) async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.payment.method, {"amount": (amount * 100).round().toString().padLeft(12, '0'), "currencyPosition": "CURRENCY_BEFORE_AMOUNT", "currencyCode": 986, "paymentType": "VOUCHER", "callerId": callerId, "orderId": orderId, "allowPrintCurrentTransaction": allowPrintCurrentTransaction, "creditType": "", "installments": 1});
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from pix payment
  Future<bool> pixPayment({required double amount, required String callerId, required String orderId, bool allowPrintCurrentTransaction = false}) async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.payment.method, {"amount": (amount * 100).round().toString().padLeft(12, '0'), "currencyPosition": "CURRENCY_BEFORE_AMOUNT", "currencyCode": 986, "paymentType": "PIX", "callerId": callerId, "orderId": orderId, "allowPrintCurrentTransaction": allowPrintCurrentTransaction, "creditType": "", "installments": 1});
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from refund payment
  Future<bool> refundPayment({required double amount, required String callerId, required String transactionDate, required String cvNumber, String? originTerminal, bool allowPrintCurrentTransaction = false}) async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.paymentRefund.method, {"amount": (amount * 100).round().toString().padLeft(12, '0'), "callerId": callerId, "transactionDate": transactionDate, "cvNumber": cvNumber, "originTerminal": originTerminal ?? "", "allowPrintCurrentTransaction": allowPrintCurrentTransaction});
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from pre-authorization
  Future<bool> preAuthorization({required double amount, required String callerId, required String orderId, bool allowPrintCurrentTransaction = false}) async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.preAuthorization.method, {"amount": (amount * 100).round().toString().padLeft(12, '0'), "currencyPosition": "CURRENCY_BEFORE_AMOUNT", "currencyCode": 986, "callerId": callerId, "orderId": orderId, "allowPrintCurrentTransaction": allowPrintCurrentTransaction});
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from payment status
  Future<bool> paymentStatus({required String callerId, bool allowPrintCurrentTransaction = false}) async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.paymentStatus.method, {"callerId": callerId, "allowPrintCurrentTransaction": allowPrintCurrentTransaction});
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from print
  Future<bool> print({required GetnetPrinterParams printerParams}) async {
    try {
      await channel.invokeMethod(PaymentTypeCall.print.method, printerParams.toMap());
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Function to invoke method from reprint
  Future<bool> reprint() async {
    try {
      await channel.invokeMethod(PaymentTypeCall.reprint.method);
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Function to get device info
  Future<bool> getDeviceInfo() async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.deviceInfo.method);
      return result != null;
    } catch (e) {
      return false;
    }
  }

  /// Function to get info
  Future<bool> getInfo() async {
    try {
      final result = await channel.invokeMethod(PaymentTypeCall.info.method);
      return result != null;
    } catch (e) {
      return false;
    }
  }
}
