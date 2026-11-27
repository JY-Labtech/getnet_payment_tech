import 'package:flutter/services.dart';
import 'package:getnet_payment_tech/src/interface/handler/igetnet_handler.dart';

import 'payments/payment.dart';

class GetnetTech {
  static const channelName = "getnet_payment_tech";
  final MethodChannel _channel;
  Payment? _payment;

  static GetnetTech? _instance;

  GetnetTech(this._channel);

  static GetnetTech get I {
    _instance ??= GetnetTech(const MethodChannel(channelName));
    return _instance!;
  }

  /// Initializes the plugin and registers the notification [handler].
  ///
  /// Calling it again only replaces the handler. Throws `PlatformException`
  /// (e.g. `NATIVE_UNAVAILABLE`) when the native implementation or the Getnet
  /// SDK (libposdigital) is not available in the app.
  Future<Payment> initPayment({required IGetnetHandler handler}) async {
    if (_payment != null) {
      await _payment!.ensureInitialised(handler: handler);
      return _payment!;
    }
    _payment = await Payment.create(channel: _channel, paymentHandler: handler);
    return _payment!;
  }

  /// Get the Payment object. Note: It needs to be initialized.
  Payment get payment {
    if (_payment == null) {
      throw "PAYMENT NEEDS TO BE INITIALIZED! \n TRY: GetnetTech.I.initPayment(handler: handler)";
    }
    return _payment!;
  }
}
