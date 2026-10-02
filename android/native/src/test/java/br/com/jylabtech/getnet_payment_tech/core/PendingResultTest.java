package br.com.jylabtech.getnet_payment_tech.core;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import br.com.jylabtech.getnet_payment_tech.usecase.FakeResult;

public class PendingResultTest {

    @Test
    public void takeReturnsResultOnlyOnce() {
        PendingResult pending = new PendingResult();
        FakeResult result = new FakeResult();

        pending.set(result);

        assertSame(result, pending.take());
        assertNull(pending.take());
    }

    @Test
    public void newOperationReplacesPreviousResult() {
        PendingResult pending = new PendingResult();
        FakeResult first = new FakeResult();
        FakeResult second = new FakeResult();

        pending.set(first);
        assertSame(first, pending.take());
        pending.set(second);

        assertSame(second, pending.take());
        assertNull(pending.take());
    }

    @Test
    public void takeWithoutPendingOperationReturnsNull() {
        assertNull(new PendingResult().take());
    }
}
