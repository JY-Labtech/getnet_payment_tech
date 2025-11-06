import 'package:getnet_payment_tech/src/interface/utils/types/payment_type_handler.dart';

extension PaymentTypeHandlerExt on PaymentTypeHandler {
  String get method {
    Map<PaymentTypeHandler, String> paymentTypeToStringMap = {
      PaymentTypeHandler.onTransactionSuccess: "onTransactionSuccess",
      PaymentTypeHandler.onError: "onError",
      PaymentTypeHandler.onMessage: "onMessage",
      PaymentTypeHandler.onLoading: "onLoading",
      PaymentTypeHandler.onFinishedResponse: "onFinishedResponse",
      PaymentTypeHandler.onChanged: "onChanged",
    };

    return paymentTypeToStringMap[this] ?? (throw "Not Implemented");
  }
}

