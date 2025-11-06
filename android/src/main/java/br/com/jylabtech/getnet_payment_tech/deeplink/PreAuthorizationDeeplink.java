package br.com.jylabtech.getnet_payment_tech.deeplink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import br.com.jylabtech.getnet_payment_tech.core.RequestConstants;
import android.app.Activity;

public class PreAuthorizationDeeplink extends Deeplink {
    @Override
    public Bundle startDeeplink(Activity binding, Bundle bundle) {
        try {
            String amount = bundle.getString("amount");
            String currencyPosition = bundle.getString("currencyPosition");
            int currencyCode = bundle.getInt("currencyCode");
            String callerId = bundle.getString("callerId");
            boolean allowPrintCurrentTransaction = bundle.getBoolean("allowPrintCurrentTransaction", false);
            String orderId = bundle.getString("orderId");

            if (amount == null) {
                throw new IllegalArgumentException("Invalid pre authorization details: amount");
            }
            if (currencyPosition == null) {
                throw new IllegalArgumentException("Invalid pre authorization details: currencyPosition");
            }
            if (callerId == null) {
                throw new IllegalArgumentException("Invalid pre authorization details: callerId");
            }

            Uri uri = new Uri.Builder()
                    .scheme("getnet")
                    .authority("pagamento")
                    .appendPath("v2")
                    .appendPath("pre-authorization")
                    .appendQueryParameter("amount", amount)
                    .appendQueryParameter("currencyPosition", currencyPosition)
                    .appendQueryParameter("currencyCode", String.valueOf(currencyCode))
                    .appendQueryParameter("callerId", callerId)
                    .appendQueryParameter("allowPrintCurrentTransaction", String.valueOf(allowPrintCurrentTransaction))
                    .appendQueryParameter("orderId", orderId)
                    .build();

            Intent paymentIntent = new Intent(Intent.ACTION_VIEW);
            paymentIntent.setData(uri);
            binding.startActivityForResult(paymentIntent, RequestConstants.PRE_AUTHORIZE_REQUEST_CODE);

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

