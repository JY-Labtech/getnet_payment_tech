import 'package:getnet_payment_tech/src/interface/utils/types/payment_type_handler.dart';

extension StringPaymentHandlerExt on String {
  PaymentTypeHandler get handler {
    Map<String, PaymentTypeHandler> paymentTypeMap = {
      "onTransactionSuccess": PaymentTypeHandler.onTransactionSuccess,
      "onError": PaymentTypeHandler.onError,
      "onMessage": PaymentTypeHandler.onMessage,
      "onFinishedResponse": PaymentTypeHandler.onFinishedResponse,
      "onLoading": PaymentTypeHandler.onLoading,
      "onChanged": PaymentTypeHandler.onChanged,
    };

    return paymentTypeMap[this] ?? (throw "Not Implemented");
  }
}

