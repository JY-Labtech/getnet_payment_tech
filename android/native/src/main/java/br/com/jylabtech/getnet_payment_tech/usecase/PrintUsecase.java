package br.com.jylabtech.getnet_payment_tech.usecase;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;

import br.com.jylabtech.getnet_payment_tech.services.PrintService;

public class PrintUsecase {

    private Result resultScope;
    private PrintService printService;

    public PrintUsecase(Result result) {
        this.resultScope = result;
        this.printService = new PrintService();
    }

    public void print(MethodCall call) {
        List<HashMap<String, Object>> listPrintContent = call.argument("printable_content");
        Bundle bundleResult = printService.start(listPrintContent != null ? toBundleList(listPrintContent) : new ArrayList<>());

        if ("SUCCESS".equals(bundleResult.getString("code"))) {
            Map<String, Object> data = new HashMap<>();
            data.put("code", "SUCCESS");
            data.put("message", bundleResult.getString("message"));
            resultScope.success(data);
        } else {
            String message = bundleResult.getString("message") != null ? bundleResult.getString("message") : "result error";
            resultScope.error(bundleResult.getString("code") != null ? bundleResult.getString("code") : "ERROR", message, null);
            resultScope = null;
        }

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

