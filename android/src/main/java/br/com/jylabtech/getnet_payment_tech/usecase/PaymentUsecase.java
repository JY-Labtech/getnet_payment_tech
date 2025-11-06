package br.com.jylabtech.getnet_payment_tech.usecase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Map;

import br.com.jylabtech.getnet_payment_tech.deeplink.PaymentDeeplink;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

public class PaymentUsecase {

    private Activity binding;
    private Result resultScope;

    private PaymentDeeplink paymentDeeplink;

    public PaymentUsecase(Result result, Activity bind) {
        this.resultScope = result;
        this.binding = bind;
        this.paymentDeeplink = new PaymentDeeplink();
    }

    public void starDeeplink( Bundle bundle) {
        try {
            Bundle bundleStartDeeplink = paymentDeeplink.startDeeplink(this.binding, bundle);
        String code = bundleStartDeeplink.getString("code", "ERROR");

        if ("ERROR".equals(code)) {
            String message = bundleStartDeeplink.getString("message", "start deeplink error");
            resultScope.error(code, message, null);
        }
        } catch (Exception e) {
            resultScope.error("ERROR", e.getMessage(), null);
        }
    }

    public Map<String, Object> validate(Intent intent) {
        return paymentDeeplink.validateIntent(intent);
    }
}

