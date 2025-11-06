package br.com.jylabtech.getnet_payment_tech.usecase;

import br.com.jylabtech.getnet_payment_tech.BuildConfig;
import br.com.jylabtech.getnet_payment_tech.licence.CheckLicenceTech;
import br.com.jylabtech.getnet_payment_tech.licence.LicenceResult;

import android.content.Context;
import android.util.Log;

public class LicenceUsecase {
    private static final String TAG = "LicenceUsecase";
    private final Context context;
    private CheckLicenceTech licenceTech;

    public LicenceUsecase(Context context) {
        this.context = context;
        this.licenceTech = new CheckLicenceTech(context);
    }

    public boolean checkValidLicence() throws Exception {
        String licenceKeyLocal = BuildConfig.LICENCE_KEY;
        String licenceInternalKey = BuildConfig.LICENCE_INTERNAL_KEY;

        Log.i(TAG, "**Licença local: " + licenceKeyLocal);

        LicenceResult resultLicence = licenceTech.validateLicence(licenceKeyLocal, licenceInternalKey);

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
        boolean result = licenceTech.validateExistsLicence();
        Log.i(TAG, "*Licença existente válida: " + result);
        return result;
    }
}

