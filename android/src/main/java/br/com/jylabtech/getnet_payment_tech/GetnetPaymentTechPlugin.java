package br.com.jylabtech.getnet_payment_tech;

import androidx.annotation.NonNull;

import br.com.jylabtech.getnet_payment_tech.core.GetnetTech;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.util.Map;

import com.getnet.posdigital.PosDigital;

public class GetnetPaymentTechPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private static final String CHANNEL_NAME = "getnet_payment_tech";
    private MethodChannel channel;
    private Activity binding;
    private MethodChannel.Result resultScope;
    private Context context;

    private GetnetTech getnetTech;

    private void checkGetnetTech() {
        if(this.getnetTech == null) {
            if(this.context != null && this.channel != null && this.binding != null) {
                this.getnetTech = new GetnetTech(this.context, this.channel, this.binding);
            }
        }
    }

    @Override
    public void onAttachedToEngine(FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();
        checkGetnetTech();
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding newBinding) {
        this.binding = newBinding.getActivity();
        PosDigital.register(newBinding.getActivity(), bindCallback);
        checkGetnetTech();
        newBinding.addActivityResultListener((requestCode, resultCode, intent) -> {
            Map<String, Object> resultCheck = getnetTech.checkIntentResponse(requestCode, resultCode, intent);
            if(resultCheck != null) {
                sendResultData(resultCheck);
            }
            return true;
        });
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.binding = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding newBinding) {
        this.binding = newBinding.getActivity();
        onAttachedToActivity(newBinding);
        checkGetnetTech();
    }

    @Override
    public void onDetachedFromActivity() {
        try {
            if (PosDigital.getInstance().isInitiated()) {
                PosDigital.unregister(binding);
                binding = null;
            }
        } catch (Exception e) {
            Log.e("error", "Erro de exception no Destroy da Activity");
        }
    }

    @Override
    public void onDetachedFromEngine(FlutterPlugin.FlutterPluginBinding newBinding) {
        channel.setMethodCallHandler(null);
        try {
            if (PosDigital.getInstance().isInitiated()) {
                PosDigital.unregister(this.binding);
            }
        } catch (Exception e) {
            Log.e("error", "Erro de exception no Destroy da Activity");
        }
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        resultScope = result;

        if (!(this.binding instanceof Activity)) {
            resultScope.error("UNAVAILABLE", "Activity is not available", null);
            return;
        }

        checkGetnetTech();

        getnetTech.callMethod(call, result);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void sendResultData(Map<String, Object> paymentData) {
        if (paymentData != null) {
            try {
                // Converte o resultado para JSON string
                String jsonData = convertMapToJson(paymentData);
                
                if ("SUCCESS".equals(paymentData.get("code")) || "PENDING".equals(paymentData.get("code"))) {
                    // Envia através do MethodChannel para disparar o handler onFinishedResponse
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        channel.invokeMethod("onFinishedResponse", jsonData, new MethodChannel.Result() {
                            @Override
                            public void success(Object result) {
                                // Handler foi chamado com sucesso
                            }
                            
                            @Override
                            public void error(String errorCode, String errorMessage, Object errorDetails) {
                                Log.e("GetnetPaymentTech", "Error calling handler: " + errorMessage);
                            }
                            
                            @Override
                            public void notImplemented() {
                                Log.w("GetnetPaymentTech", "Handler not implemented");
                            }
                        });
                        
                        // Também retorna o resultado original
                        if (resultScope != null) {
                            resultScope.success(paymentData);
                        }
                    });
                } else {
                    // Envia erro através do MethodChannel para disparar o handler onError
                    String message = (String) paymentData.getOrDefault("message", "Erro desconhecido");
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        channel.invokeMethod("onError", message, new MethodChannel.Result() {
                            @Override
                            public void success(Object result) {
                                // Handler foi chamado com sucesso
                            }
                            
                            @Override
                            public void error(String errorCode, String errorMessage, Object errorDetails) {
                                Log.e("GetnetPaymentTech", "Error calling error handler: " + errorMessage);
                            }
                            
                            @Override
                            public void notImplemented() {
                                Log.w("GetnetPaymentTech", "Error handler not implemented");
                            }
                        });
                        
                        if (resultScope != null) {
                            resultScope.error((String) paymentData.getOrDefault("code", "ERROR"), message, null);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("GetnetPaymentTech", "Error processing result: " + e.getMessage());
                if (resultScope != null) {
                    resultScope.error("ERROR", "Erro ao processar resultado: " + e.getMessage(), null);
                }
            }
        }
    }
    
    private String convertMapToJson(Map<String, Object> map) {
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Map) {
                    jsonObject.put(entry.getKey(), new org.json.JSONObject((Map) value));
                } else {
                    jsonObject.put(entry.getKey(), value);
                }
            }
            return jsonObject.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    private PosDigital.BindCallback bindCallback = new PosDigital.BindCallback() {
        @Override
        public void onError(Exception e) {
            if (PosDigital.getInstance().isInitiated()) {
                PosDigital.unregister(binding);
            }
            connectPosDigitalService();
        }

        @Override
        public void onConnected() {
            Log.d("Register PosDigital", "PosDigital connected successfully!");
        }

        @Override
        public void onDisconnected() {
            Log.d("Register PosDigital", "PosDigital disconnected!");
        }
    };

    private void connectPosDigitalService() {
        PosDigital.register(binding, bindCallback);
    }
}
