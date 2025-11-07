package br.com.jylabtech.getnet_payment_tech;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * Classe bridge que delega toda a lógica ao artefato nativo empacotado em AAR.
 */
public final class GetnetPaymentTechPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {

    private static final String IMPLEMENTATION_CLASS = "br.com.jylabtech.getnet_payment_tech.GetnetPaymentTechPluginImpl";

    private final FlutterPlugin delegatePlugin;
    private final MethodChannel.MethodCallHandler delegateHandler;
    private final ActivityAware delegateActivityAware;

    public GetnetPaymentTechPlugin() {
        Object implementation = instantiateImplementation();
        if (!(implementation instanceof FlutterPlugin)) {
            throw new IllegalStateException("A implementação nativa não implementa FlutterPlugin.");
        }
        this.delegatePlugin = (FlutterPlugin) implementation;
        this.delegateHandler = implementation instanceof MethodChannel.MethodCallHandler
                ? (MethodChannel.MethodCallHandler) implementation
                : null;
        this.delegateActivityAware = implementation instanceof ActivityAware ? (ActivityAware) implementation : null;
    }

    private Object instantiateImplementation() {
        try {
            Class<?> implClass = Class.forName(IMPLEMENTATION_CLASS);
            return implClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Implementação nativa não encontrada. Certifique-se de executar o script de build do AAR.", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Falha ao instanciar a implementação nativa do plugin.", exception);
        }
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        delegatePlugin.onAttachedToEngine(binding);
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        delegatePlugin.onDetachedFromEngine(binding);
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if (delegateHandler != null) {
            delegateHandler.onMethodCall(call, result);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        if (delegateActivityAware != null) {
            delegateActivityAware.onAttachedToActivity(binding);
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        if (delegateActivityAware != null) {
            delegateActivityAware.onDetachedFromActivityForConfigChanges();
        }
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        if (delegateActivityAware != null) {
            delegateActivityAware.onReattachedToActivityForConfigChanges(binding);
        }
    }

    @Override
    public void onDetachedFromActivity() {
        if (delegateActivityAware != null) {
            delegateActivityAware.onDetachedFromActivity();
        }
    }
}
