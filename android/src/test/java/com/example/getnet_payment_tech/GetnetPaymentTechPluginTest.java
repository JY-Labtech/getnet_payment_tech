package com.example.getnet_payment_tech;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import br.com.jylabtech.getnet_payment_tech.GetnetPaymentTechPlugin;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.plugin.common.MethodChannel;
import org.junit.Test;

/**
 * Testa a bridge Java do plugin com a implementação nativa pré-compilada em android/libs.
 *
 * Rode a partir do example: `cd example/android && ./gradlew :getnet_payment_tech:testDebugUnitTest`.
 */
public class GetnetPaymentTechPluginTest {
  private static final String IMPLEMENTATION_CLASS =
      "br.com.jylabtech.getnet_payment_tech.GetnetPaymentTechPluginImpl";

  @Test
  public void nativeImplementation_isPackagedInLibs() throws Exception {
    // initialize=false: só verifica que a classe está no jar pré-compilado, sem carregar o SDK da Getnet.
    Class<?> implClass = Class.forName(IMPLEMENTATION_CLASS, false, getClass().getClassLoader());

    assertTrue(FlutterPlugin.class.isAssignableFrom(implClass));
    assertTrue(MethodChannel.MethodCallHandler.class.isAssignableFrom(implClass));
    assertTrue(ActivityAware.class.isAssignableFrom(implClass));
  }

  @Test
  public void constructor_withNativeImplementation_createsBridge() {
    assertNotNull(new GetnetPaymentTechPlugin());
  }
}
