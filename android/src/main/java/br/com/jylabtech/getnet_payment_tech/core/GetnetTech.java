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
import android.util.Log;
import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class GetnetTech {
    private static final String TAG = "GetnetTech";
    final Context currentContext;
    final MethodChannel mChannel;
    private MethodChannel.Result resultScope;
    private Activity binding;
    private PaymentUsecase paymentUsecase;
    private InfoUsecase infoUsecase;
    private PreAuthorizeUsecase preAuthorizeUsecase;
    private RefundUsecase refundUsecase;
    private ReprintUsecase reprintUsecase;
    private StatusUsecase statusUsecase;
    private PrintUsecase printUsecase;
    private LicenceUsecase licenceUsecase;

    public GetnetTech(Context currentContext, MethodChannel channel, Activity activity) {
        this.currentContext = currentContext;
        this.mChannel = channel;
        this.binding = activity;
    }

    public void callMethod(MethodCall call, MethodChannel.Result result) {
        this.resultScope = result;
        
        // Verifica licença antes de permitir qualquer operação
        try {
            if (licenceUsecase == null) {
                licenceUsecase = new LicenceUsecase(currentContext);
            }
            
            // Valida licença existente primeiro
            boolean isValidLicence = licenceUsecase.isValidLicence();
            if (!isValidLicence) {
                // Se não tem licença válida local, tenta validar com servidor
                boolean resultLicence = licenceUsecase.checkValidLicence();
                if (!resultLicence) {
                    result.error("LICENCE_ERROR", "Sua licença não foi ativada.", null);
                    return;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao validar licença: " + e.getMessage());
            result.error("LICENCE_ERROR", e.getMessage(), null);
            return;
        }
        
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

            getPaymentUsecase().starDeeplink(bundle);
        } else if (call.method.startsWith("statusPayment")) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("allowPrintCurrentTransaction",call.argument("allowPrintCurrentTransaction"));
            bundle.putString("callerId", call.argument("callerId"));
            getStatusUsecase().starDeeplink(bundle);
        } else if (call.method.startsWith("refundPayment")) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", call.argument("amount"));
            bundle.putString("transactionDate", call.argument("transactionDate"));
            bundle.putString("cvNumber", call.argument("cvNumber"));
            bundle.putString("originTerminal", call.argument("originTerminal"));
            bundle.putBoolean("allowPrintCurrentTransaction",call.argument("allowPrintCurrentTransaction"));
            getRefundUsecase().starDeeplink(bundle);
        } else if (call.method.startsWith("preAuthorization")) {
            Bundle bundle = new Bundle();
            bundle.putString("amount", call.argument("amount"));
            bundle.putString("currencyPosition", call.argument("currencyPosition"));
            bundle.putInt("currencyCode", call.argument("currencyCode") != null ? call.argument("currencyCode") : 986);
            bundle.putBoolean("allowPrintCurrentTransaction",call.argument("allowPrintCurrentTransaction"));
            bundle.putString("callerId", call.argument("callerId"));
            bundle.putString("orderId", call.argument("orderId"));
            getPreAuthorizeUsecase().starDeeplink(bundle);
        } else if (call.method.startsWith("print")) {
            getPrintUsecase().print(call);
        } else if (call.method.startsWith("reprint")) {
            getReprintUsecase().starDeeplink(new Bundle());
        } else if (call.method.startsWith("info")) {
            getInfoUsecase().starDeeplink(new Bundle());
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

    public Map<String, Object> checkIntentResponse(int requestCode, int resultCode, Intent intent) {
        if (RESULT_OK == resultCode) {
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
        return null;
    }

    private PaymentUsecase getPaymentUsecase() {
        if(this.paymentUsecase == null) {
            this.paymentUsecase = new PaymentUsecase(this.resultScope, this.binding);
        }
        return this.paymentUsecase;
    }

    private InfoUsecase getInfoUsecase() {
        if(this.infoUsecase == null) {
            this.infoUsecase = new InfoUsecase(this.resultScope, this.binding);
        }
        return this.infoUsecase;
    }

    private PreAuthorizeUsecase getPreAuthorizeUsecase() {
        if(this.preAuthorizeUsecase == null) {
            this.preAuthorizeUsecase = new PreAuthorizeUsecase(this.resultScope, this.binding);
        }
        return this.preAuthorizeUsecase;
    }

    private RefundUsecase getRefundUsecase() {
        if(this.refundUsecase == null) {
            this.refundUsecase = new RefundUsecase(this.resultScope, this.binding);
        }
        return this.refundUsecase;
    }

    private ReprintUsecase getReprintUsecase() {
        if(this.reprintUsecase == null) {
            this.reprintUsecase = new ReprintUsecase(this.resultScope, this.binding);
        }
        return this.reprintUsecase;
    }

    private StatusUsecase getStatusUsecase() {
        if(this.statusUsecase == null) {
            this.statusUsecase = new StatusUsecase(this.resultScope, this.binding);
        }
        return this.statusUsecase;
    }

    private PrintUsecase getPrintUsecase() {
        if(this.printUsecase == null) {
            this.printUsecase = new PrintUsecase(this.resultScope);
        }
        return this.printUsecase;
    }
}

