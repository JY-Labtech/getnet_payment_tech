package br.com.jylabtech.getnet_payment_tech.usecase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Map;

import br.com.jylabtech.getnet_payment_tech.deeplink.StatusDeeplink;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

public class StatusUsecase {

    private Activity binding;
    private Result resultScope;

    private  StatusDeeplink statusDeeplink;

    public StatusUsecase(Result result, Activity bind) {
        this.resultScope = result;
        this.binding = bind;
        this.statusDeeplink = new StatusDeeplink();
    }

    public void starDeeplink( Bundle bundle) {
        Bundle bundleStartDeeplink = statusDeeplink.startDeeplink(binding, bundle);
        String code = bundleStartDeeplink.getString("code", "ERROR");

        if ("ERROR".equals(code)) {
            String message = bundleStartDeeplink.getString("message", "start deeplink error");
            resultScope.error(code, message, null);
        }
    }


    public Map<String, Object> validate(Intent intent) {
        return statusDeeplink.validateIntent(intent);
    }
}

