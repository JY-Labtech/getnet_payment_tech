package br.com.jylabtech.getnet_payment_tech.core;

import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Guarda o {@link Result} da operação de deeplink que aguarda o onActivityResult.
 * {@link #take()} devolve o Result e o limpa, garantindo uma única resposta por chamada.
 */
public final class PendingResult {
    private Result pending;

    public synchronized void set(Result result) {
        this.pending = result;
    }

    public synchronized Result take() {
        Result result = this.pending;
        this.pending = null;
        return result;
    }
}
