package br.com.jylabtech.getnet_payment_tech.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.app.Activity;
import android.os.Bundle;

import org.junit.Test;

import br.com.jylabtech.getnet_payment_tech.deeplink.InfoDeeplink;
import br.com.jylabtech.getnet_payment_tech.deeplink.PaymentDeeplink;
import br.com.jylabtech.getnet_payment_tech.deeplink.PreAuthorizationDeeplink;
import br.com.jylabtech.getnet_payment_tech.deeplink.RefundDeeplink;
import br.com.jylabtech.getnet_payment_tech.deeplink.ReprintDeeplink;
import br.com.jylabtech.getnet_payment_tech.deeplink.StatusDeeplink;

/**
 * Os usecases de deeplink devem responder no Result da chamada corrente, uma única vez,
 * mesmo reaproveitando a mesma instância (como o GetnetTech faz).
 * A falha ao abrir o deeplink é simulada com exceção (android.os.Bundle é stub em testes JVM).
 */
public class DeeplinkUsecaseResultTest {

    interface Starter {
        boolean start(FakeResult result);
    }

    private static RuntimeException failure() {
        return new IllegalStateException("deeplink indisponível");
    }

    private static void assertEachCallRepliesOnItsOwnResult(Starter starter) {
        FakeResult first = new FakeResult();
        FakeResult second = new FakeResult();

        assertFalse(starter.start(first));
        assertFalse(starter.start(second));

        assertEquals(1, first.replies());
        assertEquals("ERROR", first.errorCodes.get(0));
        assertEquals("deeplink indisponível", first.errorMessages.get(0));
        assertEquals(1, second.replies());
        assertEquals("ERROR", second.errorCodes.get(0));
    }

    @Test
    public void paymentUsecaseUsesCurrentResult() {
        PaymentUsecase usecase = new PaymentUsecase(null, new PaymentDeeplink() {
            @Override public Bundle startDeeplink(Activity binding, Bundle bundle) { throw failure(); }
        });
        assertEachCallRepliesOnItsOwnResult(result -> usecase.starDeeplink(null, result));
    }

    @Test
    public void statusUsecaseUsesCurrentResult() {
        StatusUsecase usecase = new StatusUsecase(null, new StatusDeeplink() {
            @Override public Bundle startDeeplink(Activity binding, Bundle bundle) { throw failure(); }
        });
        assertEachCallRepliesOnItsOwnResult(result -> usecase.starDeeplink(null, result));
    }

    @Test
    public void refundUsecaseUsesCurrentResult() {
        RefundUsecase usecase = new RefundUsecase(null, new RefundDeeplink() {
            @Override public Bundle startDeeplink(Activity binding, Bundle bundle) { throw failure(); }
        });
        assertEachCallRepliesOnItsOwnResult(result -> usecase.starDeeplink(null, result));
    }

    @Test
    public void preAuthorizeUsecaseUsesCurrentResult() {
        PreAuthorizeUsecase usecase = new PreAuthorizeUsecase(null, new PreAuthorizationDeeplink() {
            @Override public Bundle startDeeplink(Activity binding, Bundle bundle) { throw failure(); }
        });
        assertEachCallRepliesOnItsOwnResult(result -> usecase.starDeeplink(null, result));
    }

    @Test
    public void reprintUsecaseUsesCurrentResult() {
        ReprintUsecase usecase = new ReprintUsecase(null, new ReprintDeeplink() {
            @Override public Bundle startDeeplink(Activity binding, Bundle bundle) { throw failure(); }
        });
        assertEachCallRepliesOnItsOwnResult(result -> usecase.starDeeplink(null, result));
    }

    @Test
    public void infoUsecaseUsesCurrentResult() {
        InfoUsecase usecase = new InfoUsecase(null, new InfoDeeplink() {
            @Override public Bundle startDeeplink(Activity binding, Bundle bundle) { throw failure(); }
        });
        assertEachCallRepliesOnItsOwnResult(result -> usecase.starDeeplink(null, result));
    }
}
