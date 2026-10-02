package br.com.jylabtech.getnet_payment_tech.usecase;

import java.util.ArrayList;
import java.util.List;

import io.flutter.plugin.common.MethodChannel;

/** Result que registra as respostas recebidas (cada chamada deve receber exatamente uma). */
public class FakeResult implements MethodChannel.Result {
    public final List<Object> successes = new ArrayList<>();
    public final List<String> errorCodes = new ArrayList<>();
    public final List<String> errorMessages = new ArrayList<>();
    public int notImplemented;

    public int replies() {
        return successes.size() + errorCodes.size() + notImplemented;
    }

    @Override
    public void success(Object result) {
        successes.add(result);
    }

    @Override
    public void error(String errorCode, String errorMessage, Object errorDetails) {
        errorCodes.add(errorCode);
        errorMessages.add(errorMessage);
    }

    @Override
    public void notImplemented() {
        notImplemented++;
    }
}
