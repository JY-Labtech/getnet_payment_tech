package br.com.jylabtech.getnet_payment_tech.core;

import static android.app.Activity.RESULT_OK;

import br.com.jylabtech.getnet_payment_tech.services.DeviceInfo;
import br.com.jylabtech.getnet_payment_tech.usecase.InfoUsecase;
import br.com.jylabtech.getnet_payment_tech.usecase.LicenceUsecase;
import br.com.jylabtech.getnet_payment_tech.usecase.PaymentUsecase;
import br.com.jylabtech.getnet_payment_tech.usecase.PreAuthorizeUsecase;
import br.com.jylabtech.getnet_payment_tech.usecase.PrintUsecase;
import br.com.jylabtech.getnet_payment_tech.usecase.RefundUsecase;
import br.com.jylabtech.getnet_payment_tech.usecase.ReprintUsecase;
import br.com.jylabtech.getnet_payment_tech.usecase.StatusUsecase;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetnetTech {
    private static final String TAG = "GetnetTech";
    final Context currentContext;
    final MethodChannel mChannel;
    private final PendingResult pendingDeeplinkResult = new PendingResult();
    private Activity binding;
    private PaymentUsecase paymentUsecase;
    private InfoUsecase infoUsecase;
    private PreAuthorizeUsecase preAuthorizeUsecase;
    private RefundUsecase refundUsecase;
    private ReprintUsecase reprintUsecase;
    private StatusUsecase statusUsecase;
    private PrintUsecase printUsecase;
    private LicenceUsecase licenceUsecase;
    private String licenceKey;
    private String licenceInternalKey;
    private final ExecutorService licenceExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public GetnetTech(Context currentContext, MethodChannel channel, Activity activity) {
        this.currentContext = currentContext;
        this.mChannel = channel;
        this.binding = activity;
    }

    public void callMethod(MethodCall call, MethodChannel.Result result) {
        if ("initPayment".equals(call.method)) {
            handleInitPayment(call, result);
            return;
        }

        // Gate de licença: toda operação (pagamento, estorno, status, pré-autorização,
        // impressão, reimpressão, info, deviceInfo) só executa com licença válida.
        withValidLicence(result, () -> dispatchMethod(call, result));
    }

    private void dispatchMethod(MethodCall call, MethodChannel.Result result) {
        if (call.method.startsWith("payment")) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", call.argument("amount"));
            bundle.putString("currencyPosition", call.argument("currencyPosition"));
            bundle.putInt("currencyCode", call.argument("currencyCode") != null ? call.argument("currencyCode") : 986);
            String paymentType = call.argument("paymentType");
            bundle.putString("paymentType", paymentType);
            bundle.putString("callerId", call.argument("callerId"));
            bundle.putString("orderId", call.argument("orderId"));
            bundle.putBoolean("allowPrintCurrentTransaction", call.argument("allowPrintCurrentTransaction"));
            bundle.putString("creditType", call.argument("creditType"));
            bundle.putInt("installments", call.argument("installments"));

            startDeeplink(getPaymentUsecase().starDeeplink(bundle, result), result);
        } else if (call.method.startsWith("statusPayment")) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("allowPrintCurrentTransaction",call.argument("allowPrintCurrentTransaction"));
            bundle.putString("callerId", call.argument("callerId"));
            startDeeplink(getStatusUsecase().starDeeplink(bundle, result), result);
        } else if (call.method.startsWith("refundPayment")) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", call.argument("amount"));
            bundle.putString("transactionDate", call.argument("transactionDate"));
            bundle.putString("cvNumber", call.argument("cvNumber"));
            bundle.putString("originTerminal", call.argument("originTerminal"));
            bundle.putBoolean("allowPrintCurrentTransaction",call.argument("allowPrintCurrentTransaction"));
            startDeeplink(getRefundUsecase().starDeeplink(bundle, result), result);
        } else if (call.method.startsWith("preAuthorization")) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", call.argument("amount"));
            bundle.putString("currencyPosition", call.argument("currencyPosition"));
            bundle.putInt("currencyCode", call.argument("currencyCode") != null ? call.argument("currencyCode") : 986);
            bundle.putBoolean("allowPrintCurrentTransaction",call.argument("allowPrintCurrentTransaction"));
            bundle.putString("callerId", call.argument("callerId"));
            bundle.putString("orderId", call.argument("orderId"));
            startDeeplink(getPreAuthorizeUsecase().starDeeplink(bundle, result), result);
        } else if (call.method.startsWith("print")) {
            getPrintUsecase().print(call, result);
        } else if (call.method.startsWith("reprint")) {
            startDeeplink(getReprintUsecase().starDeeplink(new Bundle(), result), result);
        } else if (call.method.startsWith("info")) {
            startDeeplink(getInfoUsecase().starDeeplink(new Bundle(), result), result);
        } else if (call.method.startsWith("deviceInfo")) {
            Map<String, Object> deviceResponse = new DeviceInfo().getSerialNumberAndDeviceModel();
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("code", "SUCCESS");
            resultData.put("data", deviceResponse);
            result.success(resultData);
        } else {
            result.notImplemented();
        }
    }

    /** Deeplink aberto: guarda o Result da chamada corrente para responder no onActivityResult. */
    private void startDeeplink(boolean started, MethodChannel.Result result) {
        if (started) {
            pendingDeeplinkResult.set(result);
        }
    }

    /**
     * Devolve (e limpa) o Result da operação de deeplink pendente, ou {@code null} se não houver.
     * Cada Result é entregue uma única vez.
     */
    public MethodChannel.Result takePendingDeeplinkResult() {
        return pendingDeeplinkResult.take();
    }

    private void handleInitPayment(MethodCall call, MethodChannel.Result result) {
        String licenceKeyArgument = call.argument("licenceKey");
        String licenceInternalKeyArgument = call.argument("licenceInternalKey");

        if (licenceKeyArgument == null || licenceKeyArgument.trim().isEmpty()) {
            result.error("LICENCE_ERROR", "licenceKey é obrigatório.", null);
            return;
        }

        this.licenceKey = licenceKeyArgument;
        // licenceInternalKey é opcional: a URL efetiva é sempre a do servidor oficial
        // (LicenceConfig.resolveLicenceUrl ignora valores fora dele).
        this.licenceInternalKey = licenceInternalKeyArgument != null ? licenceInternalKeyArgument : "";

        if (licenceUsecase == null) {
            licenceUsecase = new LicenceUsecase(currentContext);
        }

        licenceUsecase.updateLicenceKeys(licenceKey, licenceInternalKey);

        withValidLicence(result, () -> result.success(true));
    }

    /**
     * Executa {@code onValid} somente se a licença for válida. Primeiro verifica o cache local
     * (sem rede); se não houver cache válido, consulta o servidor em background e responde na
     * main thread. Licença inválida devolve LICENCE_ERROR e a operação não é executada.
     */
    private void withValidLicence(MethodChannel.Result result, Runnable onValid) {
        final LicenceUsecase usecase = this.licenceUsecase;
        if (usecase == null || licenceKey == null) {
            result.error("LICENCE_ERROR", "Chame initPayment antes de usar os métodos do plugin.", null);
            return;
        }

        boolean cachedValid = false;
        try {
            cachedValid = usecase.isValidLicence();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao verificar licença local: " + e.getMessage());
        }

        if (cachedValid) {
            onValid.run();
            return;
        }

        licenceExecutor.execute(() -> {
            String errorMessage = null;
            boolean valid = false;
            try {
                valid = usecase.checkValidLicence();
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }

            final boolean isValid = valid;
            final String message = errorMessage != null ? errorMessage : "Sua licença não foi ativada.";
            mainHandler.post(() -> {
                if (isValid) {
                    onValid.run();
                } else {
                    Log.e(TAG, "Licença inválida: " + message);
                    result.error("LICENCE_ERROR", message, null);
                }
            });
        });
    }

    /**
     * Converte o onActivityResult de um deeplink em resposta. Devolve {@code null} para requestCodes
     * que não são do plugin. Se o app da Getnet voltar sem RESULT_OK (ex.: cancelamento), devolve
     * erro {@code CANCELED} para que o Future pendente complete.
     */
    public Map<String, Object> checkIntentResponse(int requestCode, int resultCode, Intent intent) {
        if (!ActivityResultRules.isPluginRequestCode(requestCode)) {
            return null;
        }
        if (RESULT_OK != resultCode) {
            return ActivityResultRules.notOkResponse(resultCode);
        }
        Map<String, Object> responseMap = new HashMap<>();
        switch (requestCode) {
            case RequestConstants.PAYMENT_REQUEST_CODE:
                responseMap = getPaymentUsecase().validate(intent);
                break;
            case RequestConstants.STATUS_REQUEST_CODE:
                responseMap = getStatusUsecase().validate(intent);
                break;
            case RequestConstants.PRE_AUTHORIZE_REQUEST_CODE:
                responseMap = getPreAuthorizeUsecase().validate(intent);
                break;
            case RequestConstants.REFUND_REQUEST_CODE:
                responseMap = getRefundUsecase().validate(intent);
                break;
            case RequestConstants.REPRINT_REQUEST_CODE:
                responseMap = getReprintUsecase().validate(intent);
                break;
            case RequestConstants.INFO_REQUEST_CODE:
                responseMap = getInfoUsecase().validate(intent);
                break;
        }
        return responseMap;
    }

    private PaymentUsecase getPaymentUsecase() {
        if(this.paymentUsecase == null) {
            this.paymentUsecase = new PaymentUsecase(this.binding);
        }
        return this.paymentUsecase;
    }

    private InfoUsecase getInfoUsecase() {
        if(this.infoUsecase == null) {
            this.infoUsecase = new InfoUsecase(this.binding);
        }
        return this.infoUsecase;
    }

    private PreAuthorizeUsecase getPreAuthorizeUsecase() {
        if(this.preAuthorizeUsecase == null) {
            this.preAuthorizeUsecase = new PreAuthorizeUsecase(this.binding);
        }
        return this.preAuthorizeUsecase;
    }

    private RefundUsecase getRefundUsecase() {
        if(this.refundUsecase == null) {
            this.refundUsecase = new RefundUsecase(this.binding);
        }
        return this.refundUsecase;
    }

    private ReprintUsecase getReprintUsecase() {
        if(this.reprintUsecase == null) {
            this.reprintUsecase = new ReprintUsecase(this.binding);
        }
        return this.reprintUsecase;
    }

    private StatusUsecase getStatusUsecase() {
        if(this.statusUsecase == null) {
            this.statusUsecase = new StatusUsecase(this.binding);
        }
        return this.statusUsecase;
    }

    private PrintUsecase getPrintUsecase() {
        if(this.printUsecase == null) {
            this.printUsecase = new PrintUsecase();
        }
        return this.printUsecase;
    }
}

