package br.com.jylabtech.getnet_payment_tech.usecase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Map;

import br.com.jylabtech.getnet_payment_tech.deeplink.RefundDeeplink;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

public class RefundUsecase {

    private Activity binding;
    private Result resultScope;

    private RefundDeeplink refundDeeplink;

    public RefundUsecase(Result result, Activity bind) {
        this.resultScope = result;
        this.binding = bind;
        this.refundDeeplink = new RefundDeeplink();
    }

    public void starDeeplink( Bundle bundle) {
        Bundle bundleStartDeeplink = refundDeeplink.startDeeplink(binding, bundle);
        String code = bundleStartDeeplink.getString("code", "ERROR");

        if ("ERROR".equals(code)) {
            String message = bundleStartDeeplink.getString("message", "start deeplink error");
            resultScope.error(code, message, null);
        }
    }


    public Map<String, Object> validate(Intent intent) {
        return refundDeeplink.validateIntent(intent);
    }
}

