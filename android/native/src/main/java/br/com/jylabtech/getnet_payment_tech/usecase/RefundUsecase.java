package br.com.jylabtech.getnet_payment_tech.usecase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Map;

import br.com.jylabtech.getnet_payment_tech.deeplink.RefundDeeplink;
import io.flutter.plugin.common.MethodChannel.Result;

public class RefundUsecase {

    private final Activity binding;

    private final RefundDeeplink refundDeeplink;

    public RefundUsecase(Activity bind) {
        this(bind, new RefundDeeplink());
    }

    RefundUsecase(Activity bind, RefundDeeplink refundDeeplink) {
        this.binding = bind;
        this.refundDeeplink = refundDeeplink;
    }

    /**
     * Abre o deeplink da Getnet. Usa sempre o {@code result} da chamada corrente.
     *
     * @return {@code true} se o deeplink foi aberto (a resposta virá pelo onActivityResult);
     * {@code false} se houve erro, caso em que {@code result} já foi respondido com erro.
     */
    public boolean starDeeplink(Bundle bundle, Result result) {
        try {
            Bundle bundleStartDeeplink = refundDeeplink.startDeeplink(this.binding, bundle);
            String code = bundleStartDeeplink.getString("code", "ERROR");

            if ("ERROR".equals(code)) {
                String message = bundleStartDeeplink.getString("message", "start deeplink error");
                result.error(code, message, null);
                return false;
            }
            return true;
        } catch (Exception e) {
            result.error("ERROR", e.getMessage(), null);
            return false;
        }
    }

    public Map<String, Object> validate(Intent intent) {
        return refundDeeplink.validateIntent(intent);
    }
}
