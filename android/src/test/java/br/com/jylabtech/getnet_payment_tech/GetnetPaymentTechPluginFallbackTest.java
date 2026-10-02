package br.com.jylabtech.getnet_payment_tech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/** Sem implementação nativa ou sem SDK da Getnet, a bridge responde com erro em vez de derrubar o app. */
public class GetnetPaymentTechPluginFallbackTest {

  /** Simula o SDK da Getnet ausente (classe referenciada não encontrada ao instanciar). */
  public static class MissingSdkImplementation {
    public MissingSdkImplementation() {
      throw new NoClassDefFoundError("com/getnet/posdigital/PosDigital$BindCallback");
    }
  }

  /** Simula falha no inicializador estático (ExceptionInInitializerError também é LinkageError). */
  public static class BrokenStaticInitImplementation {
    static final int VALUE = fail();

    private static int fail() {
      throw new IllegalStateException("static init");
    }
  }

  public static class NotAPluginImplementation {
  }

  static class RecordingResult implements MethodChannel.Result {
    final List<String> errorCodes = new ArrayList<>();
    final List<String> errorMessages = new ArrayList<>();
    int successes;
    int notImplemented;

    @Override public void success(Object result) { successes++; }

    @Override public void error(String errorCode, String errorMessage, Object errorDetails) {
      errorCodes.add(errorCode);
      errorMessages.add(errorMessage);
    }

    @Override public void notImplemented() { notImplemented++; }
  }

  private static RecordingResult call(GetnetPaymentTechPlugin plugin) {
    RecordingResult result = new RecordingResult();
    plugin.onMethodCall(new MethodCall("paymentCredit", null), result);
    return result;
  }

  private static void assertUnavailable(GetnetPaymentTechPlugin plugin, String messagePart) {
    assertFalse(plugin.isNativeAvailable());
    RecordingResult result = call(plugin);
    assertEquals(1, result.errorCodes.size());
    assertEquals(0, result.successes);
    assertEquals(0, result.notImplemented);
    assertEquals(GetnetPaymentTechPlugin.UNAVAILABLE_CODE, result.errorCodes.get(0));
    assertTrue(result.errorMessages.get(0), result.errorMessages.get(0).contains(messagePart));
  }

  @Test
  public void missingGetnetSdk_repliesErrorInsteadOfCrashing() {
    assertUnavailable(new GetnetPaymentTechPlugin(MissingSdkImplementation.class.getName()), "libposdigital");
  }

  @Test
  public void brokenStaticInitializer_repliesErrorInsteadOfCrashing() {
    assertUnavailable(new GetnetPaymentTechPlugin(BrokenStaticInitImplementation.class.getName()), "libposdigital");
  }

  @Test
  public void missingNativeImplementation_repliesError() {
    assertUnavailable(new GetnetPaymentTechPlugin("br.com.jylabtech.getnet_payment_tech.DoesNotExist"), "não encontrada");
  }

  @Test
  public void implementationNotFlutterPlugin_repliesError() {
    assertUnavailable(new GetnetPaymentTechPlugin(NotAPluginImplementation.class.getName()), "FlutterPlugin");
  }

  @Test
  public void realImplementation_isAvailable() {
    assertTrue(new GetnetPaymentTechPlugin().isNativeAvailable());
  }
}
