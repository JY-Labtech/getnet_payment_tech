package br.com.jylabtech.getnet_payment_tech;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * Classe bridge que delega toda a lógica ao artefato nativo empacotado em AAR.
 *
 * Se a implementação nativa ou o SDK da Getnet (libposdigital) não estiverem disponíveis, o
 * plugin não derruba o app: registra o canal e responde toda chamada com o erro
 * {@link #UNAVAILABLE_CODE}.
 */
public final class GetnetPaymentTechPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {

    private static final String TAG = "GetnetPaymentTech";
    private static final String CHANNEL_NAME = "getnet_payment_tech";
    private static final String IMPLEMENTATION_CLASS = "br.com.jylabtech.getnet_payment_tech.GetnetPaymentTechPluginImpl";

    static final String UNAVAILABLE_CODE = "NATIVE_UNAVAILABLE";

    private final FlutterPlugin delegatePlugin;
    private final MethodChannel.MethodCallHandler delegateHandler;
    private final ActivityAware delegateActivityAware;
    private final String unavailableMessage;
    private MethodChannel fallbackChannel;

    public GetnetPaymentTechPlugin() {
        this(IMPLEMENTATION_CLASS);
    }

    GetnetPaymentTechPlugin(String implementationClass) {
        Object implementation = null;
        String error = null;
        try {
            implementation = instantiateImplementation(implementationClass);
            if (!(implementation instanceof FlutterPlugin)) {
                error = "A implementação nativa não implementa FlutterPlugin.";
                implementation = null;
            }
        } catch (IllegalStateException exception) {
            error = exception.getMessage();
        }

        if (implementation == null) {
            logError(error);
        }
        this.unavailableMessage = error;
        this.delegatePlugin = (FlutterPlugin) implementation;
        this.delegateHandler = implementation instanceof MethodChannel.MethodCallHandler
                ? (MethodChannel.MethodCallHandler) implementation
                : null;
        this.delegateActivityAware = implementation instanceof ActivityAware ? (ActivityAware) implementation : null;
    }

    private static Object instantiateImplementation(String implementationClass) {
        try {
            Class<?> implClass = Class.forName(implementationClass);
            return implClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Implementação nativa não encontrada. Certifique-se de executar o script de build do AAR.", exception);
        } catch (LinkageError error) {
            // NoClassDefFoundError/ExceptionInInitializerError: normalmente o SDK da Getnet
            // (libposdigital) não está no app.
            throw missingDependency(error);
        } catch (InvocationTargetException exception) {
            // Erro lançado dentro do construtor da implementação (ex.: NoClassDefFoundError do SDK).
            Throwable cause = exception.getCause();
            if (cause instanceof LinkageError) {
                throw missingDependency((LinkageError) cause);
            }
            throw new IllegalStateException("Falha ao instanciar a implementação nativa do plugin: " + cause, cause);
        } catch (Exception exception) {
            throw new IllegalStateException("Falha ao instanciar a implementação nativa do plugin: " + exception, exception);
        }
    }

    private static IllegalStateException missingDependency(LinkageError error) {
        return new IllegalStateException("Dependência nativa ausente ou incompatível (verifique se o app inclui o SDK da Getnet libposdigital): " + error, error);
    }

    private static void logError(String message) {
        try {
            Log.e(TAG, message);
        } catch (RuntimeException ignored) {
            // android.util.Log não está disponível em testes JVM.
        }
    }

    /** {@code true} se a implementação nativa foi carregada. */
    boolean isNativeAvailable() {
        return delegatePlugin != null;
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        if (delegatePlugin != null) {
            delegatePlugin.onAttachedToEngine(binding);
        } else {
            fallbackChannel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
            fallbackChannel.setMethodCallHandler(this);
        }
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        if (delegatePlugin != null) {
            delegatePlugin.onDetachedFromEngine(binding);
        } else if (fallbackChannel != null) {
            fallbackChannel.setMethodCallHandler(null);
            fallbackChannel = null;
        }
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if (delegateHandler != null) {
            delegateHandler.onMethodCall(call, result);
        } else if (delegatePlugin == null) {
            result.error(UNAVAILABLE_CODE, unavailableMessage, null);
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
