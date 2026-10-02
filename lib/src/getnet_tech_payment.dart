import 'package:flutter/services.dart';
import 'package:getnet_payment_tech/src/interface/handler/igetnet_handler.dart';

import 'licence/constants/licence_constants.dart';
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

  /// Function to initialize payment and register the notification handler.
  ///
  /// [licenceInternalKey] é opcional: por padrão usa [licenceCheck] (servidor
  /// oficial da JY Labtech). Valores que não apontem para o servidor oficial
  /// são ignorados pelo nativo. Lança `PlatformException(code: 'LICENCE_ERROR')`
  /// se a licença for inválida, vencida, suspensa ou cancelada.
  Future<Payment> initPayment({
    required IGetnetHandler handler,
    required String licenceKey,
    String licenceInternalKey = licenceCheck,
  }) async {
    if (_payment != null) {
      await _payment!.ensureInitialised(
        licenceKey: licenceKey,
        licenceInternalKey: licenceInternalKey,
        handler: handler,
      );
      return _payment!;
    }
    _payment = await Payment.create(
      channel: _channel,
      paymentHandler: handler,
      licenceKey: licenceKey,
      licenceInternalKey: licenceInternalKey,
    );
    return _payment!;
  }

  /// Get the Payment object. Note: It needs to be initialized.
  Payment get payment {
    if (_payment == null) {
      throw "PAYMENT NEEDS TO BE INITIALIZED! \n TRY: GetnetTech.I.initPayment(handler: handler, licenceKey: '<KEY>')";
    }
    return _payment!;
  }
}
