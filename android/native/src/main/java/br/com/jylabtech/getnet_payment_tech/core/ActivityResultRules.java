package br.com.jylabtech.getnet_payment_tech.core;

import java.util.HashMap;
import java.util.Map;

/** Regras puras (sem Android) para tratar o onActivityResult dos deeplinks da Getnet. */
public final class ActivityResultRules {

    /** Valor de {@code Activity.RESULT_OK}. */
    public static final int RESULT_OK = -1;

    public static final String CANCELED_CODE = "CANCELED";

    private ActivityResultRules() {
    }

    /** {@code true} somente para os requestCodes usados pelos deeplinks deste plugin. */
    public static boolean isPluginRequestCode(int requestCode) {
        switch (requestCode) {
            case RequestConstants.PAYMENT_REQUEST_CODE:
            case RequestConstants.STATUS_REQUEST_CODE:
            case RequestConstants.PRE_AUTHORIZE_REQUEST_CODE:
            case RequestConstants.REFUND_REQUEST_CODE:
            case RequestConstants.REPRINT_REQUEST_CODE:
            case RequestConstants.INFO_REQUEST_CODE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Resposta para o app da Getnet que voltou sem RESULT_OK (cancelado pelo usuário, botão voltar,
     * app fechado). Devolve {@code null} quando o resultCode é RESULT_OK.
     */
    public static Map<String, Object> notOkResponse(int resultCode) {
        if (resultCode == RESULT_OK) {
            return null;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("code", CANCELED_CODE);
        response.put("message", "Operação cancelada ou não concluída no app da Getnet (resultCode=" + resultCode + ").");
        return response;
    }
}
