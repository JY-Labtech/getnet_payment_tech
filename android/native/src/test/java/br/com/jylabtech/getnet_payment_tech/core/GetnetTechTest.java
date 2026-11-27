package br.com.jylabtech.getnet_payment_tech.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.util.Map;

import br.com.jylabtech.getnet_payment_tech.usecase.FakeResult;
import io.flutter.plugin.common.MethodCall;

/**
 * Cada chamada vai direto para a operação e responde uma única vez,
 * sem exigir chave nem acesso à rede.
 */
public class GetnetTechTest {

    private static GetnetTech tech() {
        return new GetnetTech(null, null, null);
    }

    @Test
    public void initPayment_repliesTrueWithoutArguments() {
        FakeResult result = new FakeResult();

        tech().callMethod(new MethodCall("initPayment", null), result);

        assertEquals(1, result.replies());
        assertEquals(Boolean.TRUE, result.successes.get(0));
    }

    @Test
    public void operationWithoutInitPayment_isDispatchedDirectly() {
        FakeResult result = new FakeResult();

        tech().callMethod(new MethodCall("deviceInfo", null), result);

        assertEquals(1, result.replies());
        assertEquals(1, result.successes.size());
        Map<?, ?> response = (Map<?, ?>) result.successes.get(0);
        assertEquals("SUCCESS", response.get("code"));
        assertEquals(2, ((Map<?, ?>) response.get("data")).size());
    }

    @Test
    public void unknownMethod_repliesNotImplemented() {
        FakeResult result = new FakeResult();

        tech().callMethod(new MethodCall("unknown", null), result);

        assertEquals(1, result.replies());
        assertEquals(1, result.notImplemented);
    }

    @Test
    public void noPendingDeeplinkResult_returnsNull() {
        assertNull(tech().takePendingDeeplinkResult());
    }
}
