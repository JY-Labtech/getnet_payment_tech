package br.com.jylabtech.getnet_payment_tech.usecase;

import br.com.jylabtech.getnet_payment_tech.licence.CheckLicenceTech;
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
        this.licenceInternalKey = licenceInternalKey;
    }

    public boolean checkValidLicence() throws Exception {
        ensureKeys();

        Log.i(TAG, "**Licença local: " + licenceKey);

        LicenceResult resultLicence = licenceTech.validateLicence(licenceKey, licenceInternalKey);

        if (resultLicence.getResult() == 0) {
            Log.i(TAG, "*Licença válida");
            return true;
        } else {
            String errorMessage = resultLicence.getErrorMessage();
            Log.e(TAG, "*Licença inválida: " + errorMessage);
            throw new Exception("Sua licença não foi ativada. " + (errorMessage != null ? errorMessage : ""));
        }
    }

    public boolean isValidLicence() throws Exception {
        ensureKeys();

        boolean result = licenceTech.validateExistsLicence();
        Log.i(TAG, "*Licença existente válida: " + result);
        return result;
    }

    private void ensureKeys() throws Exception {
        if (licenceKey == null || licenceKey.trim().isEmpty()) {
            throw new Exception("licenceKey não configurado. Chame initPayment no Flutter.");
        }
        if (licenceInternalKey == null || licenceInternalKey.trim().isEmpty()) {
            throw new Exception("licenceInternalKey não configurado. Chame initPayment no Flutter.");
        }
    }
}

