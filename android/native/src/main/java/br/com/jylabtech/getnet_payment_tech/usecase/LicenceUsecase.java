package br.com.jylabtech.getnet_payment_tech.usecase;

import br.com.jylabtech.getnet_payment_tech.licence.CheckLicenceTech;
import br.com.jylabtech.getnet_payment_tech.licence.LicenceConfig;
import br.com.jylabtech.getnet_payment_tech.licence.LicenceRules;
import br.com.jylabtech.getnet_payment_tech.licence.LicenceResult;

import android.content.Context;
import android.util.Log;

public class LicenceUsecase {
    private static final String TAG = "LicenceUsecase";
    private final Context context;
    private CheckLicenceTech licenceTech;
    private String licenceKey;
    private String licenceInternalKey;

    public LicenceUsecase(Context context) {
        this.context = context;
        this.licenceTech = new CheckLicenceTech(context);
    }

    public void updateLicenceKeys(String licenceKey, String licenceInternalKey) {
        this.licenceKey = licenceKey;
        // Opcional: vazio usa o servidor oficial. Valores fora do servidor oficial são ignorados
        // por LicenceConfig.resolveLicenceUrl.
        this.licenceInternalKey = (licenceInternalKey == null || licenceInternalKey.trim().isEmpty())
                ? LicenceConfig.LICENCE_CHECK_URL
                : licenceInternalKey;
    }

    /** Valida pelo cache válido ou pelo servidor. Faz acesso à rede: não chame na main thread. */
    public boolean checkValidLicence() throws Exception {
        ensureKeys();

        LicenceResult resultLicence = licenceTech.validateLicence(licenceKey, licenceInternalKey);

        if (resultLicence.getResult() == LicenceRules.RESULT_SUCCESS) {
            Log.i(TAG, "*Licença válida");
            return true;
        } else {
            String errorMessage = resultLicence.getErrorMessage();
            Log.e(TAG, "*Licença inválida: " + errorMessage);
            throw new Exception("Sua licença não foi ativada. " + (errorMessage != null ? errorMessage : ""));
        }
    }

    /** Verifica somente o cache local (sem rede), com todas as regras de validade. */
    public boolean isValidLicence() throws Exception {
        ensureKeys();

        boolean result = licenceTech.validateExistsLicence(licenceKey, licenceInternalKey);
        Log.i(TAG, "*Licença existente válida: " + result);
        return result;
    }

    private void ensureKeys() throws Exception {
        if (licenceKey == null || licenceKey.trim().isEmpty()) {
            throw new Exception("licenceKey não configurado. Chame initPayment no Flutter.");
        }
    }
}

