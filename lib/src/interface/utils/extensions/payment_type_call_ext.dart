import 'package:getnet_payment_tech/src/interface/utils/types/payment_type_call.dart';

extension PaymentTypeCallExt on PaymentTypeCall {
  String get method {
    Map<PaymentTypeCall, String> paymentTypeToStringMap = {
      PaymentTypeCall.payment: "payment",
      PaymentTypeCall.paymentCreditParc: "paymentCreditParc",
      PaymentTypeCall.paymentStatus: "statusPayment",
      PaymentTypeCall.paymentRefund: "refundPayment",
      PaymentTypeCall.preAuthorization: "preAuthorization",
      PaymentTypeCall.print: "print",
      PaymentTypeCall.reprint: "reprint",
      PaymentTypeCall.info: "info",
      PaymentTypeCall.deviceInfo: "deviceInfo",
    };

    return paymentTypeToStringMap[this] ?? (throw "Not Implemented");
  }
}

