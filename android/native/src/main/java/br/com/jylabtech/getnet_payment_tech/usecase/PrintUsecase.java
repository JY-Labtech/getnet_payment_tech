package br.com.jylabtech.getnet_payment_tech.usecase;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;

import br.com.jylabtech.getnet_payment_tech.services.PrintService;

public class PrintUsecase {

    /** Executa a impressão e devolve o resultado já convertido (código + mensagem). */
    interface PrintRunner {
        PrintOutcome print(List<HashMap<String, Object>> printableContent);
    }

    static final class PrintOutcome {
        final String code;
        final String message;

        PrintOutcome(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    private final PrintRunner printRunner;
    private PrintService printService;

    public PrintUsecase() {
        this.printRunner = this::printWithService;
    }

    PrintUsecase(PrintRunner printRunner) {
        this.printRunner = printRunner;
    }

    /** Imprime e responde uma única vez no {@code result} da chamada corrente. */
    public void print(MethodCall call, Result result) {
        PrintOutcome outcome;
        try {
            List<HashMap<String, Object>> listPrintContent = call.argument("printable_content");
            outcome = printRunner.print(listPrintContent);
        } catch (Exception e) {
            outcome = new PrintOutcome("ERROR", e.getMessage() != null ? e.getMessage() : "result error");
        }

        if (outcome != null && "SUCCESS".equals(outcome.code)) {
            Map<String, Object> data = new HashMap<>();
            data.put("code", "SUCCESS");
            data.put("message", outcome.message);
            result.success(data);
        } else {
            String code = outcome != null && outcome.code != null ? outcome.code : "ERROR";
            String message = outcome != null && outcome.message != null ? outcome.message : "result error";
            result.error(code, message, null);
        }
    }

    private PrintOutcome printWithService(List<HashMap<String, Object>> listPrintContent) {
        if (printService == null) {
            printService = new PrintService();
        }
        Bundle bundleResult = printService.start(listPrintContent != null ? toBundleList(listPrintContent) : new ArrayList<>());
        return new PrintOutcome(bundleResult.getString("code"), bundleResult.getString("message"));
    }

    private ArrayList<Bundle> toBundleList(List<HashMap<String, Object>> list) {
        ArrayList<Bundle> bundleList = new ArrayList<>();
        for (HashMap<String, Object> map : list) {
            bundleList.add(toBundle(map));
        }
        return bundleList;
    }

    private Bundle toBundle(HashMap<String, Object> map) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, (Boolean) value);
            } else if (value instanceof Double) {
                bundle.putDouble(key, (Double) value);
            } else if (value instanceof Float) {
                bundle.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                bundle.putLong(key, (Long) value);
            }
        }
        return bundle;
    }

}

