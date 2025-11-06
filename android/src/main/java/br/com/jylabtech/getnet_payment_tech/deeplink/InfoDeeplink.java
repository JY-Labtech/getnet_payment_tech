package br.com.jylabtech.getnet_payment_tech.deeplink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import br.com.jylabtech.getnet_payment_tech.core.RequestConstants;
import android.app.Activity;

public class InfoDeeplink extends Deeplink {

    @Override
    public Bundle startDeeplink(Activity binding, Bundle bundle) {
        try {
            Uri uri = new Uri.Builder()
                    .scheme("getnet")
                    .authority("pagamento")
                    .appendPath("v1")
                    .appendPath("getinfos")
                    .build();

            Intent paymentIntent = new Intent(Intent.ACTION_VIEW);
            paymentIntent.setData(uri);
            binding.startActivityForResult(paymentIntent, RequestConstants.INFO_REQUEST_CODE);

            Bundle response = new Bundle();
            response.putString("code", "SUCCESS");
            return response;
        } catch (IllegalArgumentException e) {
            return createErrorBundle(e.getMessage());
        } catch (Exception e) {
            return createErrorBundle(e.getMessage() != null ? e.getMessage() : "An unexpected error occurred");
        }
    }

    private Bundle createErrorBundle(String message) {
        Bundle errorResponse = new Bundle();
        errorResponse.putString("code", "ERROR");
        errorResponse.putString("message", message);
        return errorResponse;
    }
}

