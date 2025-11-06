package br.com.jylabtech.getnet_payment_tech.deeplink;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;

public abstract class Deeplink {
    public Bundle startDeeplink(Activity binding, Bundle bundle) {
        throw new UnsupportedOperationException();
    }

    public Map<String, Object> validateIntent(Intent intent) {
        try {
            if (intent == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "ERROR");
                errorResponse.put("message", "no intent data");
                return errorResponse;
            }

            Bundle extras = intent.getExtras();
            String status = extras != null ? extras.getString("result") : null;

            if ("0".equals(status) || "5".equals(status)) {
                Map<String, Object> data = new HashMap<>();
                for (String key : extras.keySet()) {
                    data.put(key, extras.get(key));
                }

                Map<String, Object> response = new HashMap<>();
                response.put("code", "0".equals(status) ? "SUCCESS" : "PENDING");
                response.put("data", data);
                return response;
            } else {
                String message = "Erro não identificado";
                String resultDetail = extras != null ? extras.getString("resultDetails") : null;

                switch (status) {
                    case "1": message = "Negada"; break;
                    case "2": message = "Cancelada"; break;
                    case "3": message = "Falha"; break;
                    case "4": message = "Desconhecido"; break;
                }

                if (resultDetail != null) {
                    message += "\n" + resultDetail;
                }

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "ERROR");
                errorResponse.put("message", message);
                return errorResponse;
            }
        } catch (Exception e) {
            Map<String, Object> exceptionResponse = new HashMap<>();
            exceptionResponse.put("code", "ERROR");
            exceptionResponse.put("message", String.valueOf(e));
            return exceptionResponse;
        }
    }
}

