package br.com.jylabtech.getnet_payment_tech.usecase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Map;

import br.com.jylabtech.getnet_payment_tech.deeplink.PreAuthorizationDeeplink;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

public class PreAuthorizeUsecase {

    private Activity binding;
    private Result resultScope;

    private PreAuthorizationDeeplink preAuthorizationDeeplink;

    public PreAuthorizeUsecase(Result result, Activity bind) {
        this.resultScope = result;
        this.binding = bind;
        this.preAuthorizationDeeplink = new PreAuthorizationDeeplink();
    }

    public void starDeeplink( Bundle bundle) {
        Bundle bundleStartDeeplink = preAuthorizationDeeplink.startDeeplink(binding, bundle);
        String code = bundleStartDeeplink.getString("code", "ERROR");

        if ("ERROR".equals(code)) {
            String message = bundleStartDeeplink.getString("message", "start deeplink error");
            resultScope.error(code, message, null);
        }
    }


    public Map<String, Object> validate(Intent intent) {
        return preAuthorizationDeeplink.validateIntent(intent);
    }
}

