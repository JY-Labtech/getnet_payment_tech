package br.com.jylabtech.getnet_payment_tech.deeplink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import br.com.jylabtech.getnet_payment_tech.core.RequestConstants;
import android.app.Activity;

public class RefundDeeplink extends Deeplink {
    @Override
    public Bundle startDeeplink(Activity binding, Bundle bundle) {
        try {
            
            String amount = bundle.getString("amount");
            String transactionDate = bundle.getString("transactionDate");
            String cvNumber = bundle.getString("cvNumber");
            String originTerminal = bundle.getString("originTerminal");
            boolean allowPrintCurrentTransaction = bundle.getBoolean("allowPrintCurrentTransaction", false);

            Uri.Builder uriBuilder = new Uri.Builder()
                    .scheme("getnet")
                    .authority("pagamento")
                    .appendPath("v1")
                    .appendPath("refund");

            if (amount != null && !amount.isEmpty()) {
                uriBuilder.appendQueryParameter("amount", amount);
            }
            if (transactionDate != null && !transactionDate.isEmpty()) {
                uriBuilder.appendQueryParameter("transactionDate", transactionDate);
            }
            if (cvNumber != null && !cvNumber.isEmpty()) {
                uriBuilder.appendQueryParameter("cvNumber", cvNumber);
            }
            if (originTerminal != null && !originTerminal.isEmpty()) {
                uriBuilder.appendQueryParameter("originTerminal", originTerminal);
            }

            uriBuilder.appendQueryParameter("allowPrintCurrentTransaction", String.valueOf(allowPrintCurrentTransaction));

            Intent paymentIntent = new Intent(Intent.ACTION_VIEW);
            paymentIntent.setData(uriBuilder.build());
            binding.startActivityForResult(paymentIntent, RequestConstants.REFUND_REQUEST_CODE);

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

