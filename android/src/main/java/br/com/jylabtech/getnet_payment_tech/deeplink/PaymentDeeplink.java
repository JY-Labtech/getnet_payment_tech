package br.com.jylabtech.getnet_payment_tech.deeplink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import br.com.jylabtech.getnet_payment_tech.core.RequestConstants;
import android.app.Activity;

public class PaymentDeeplink extends Deeplink {

    @Override
    public Bundle startDeeplink(Activity binding, Bundle bundle) {
        try {
            String paymentType = bundle.getString("paymentType");
            String amount = bundle.getString("amount");
            String callerId = bundle.getString("callerId");
            String currencyPosition = bundle.getString("currencyPosition");
            int currencyCode = bundle.getInt("currencyCode");
            String orderId = bundle.getString("orderId");
            boolean allowPrintCurrentTransaction = bundle.getBoolean("allowPrintCurrentTransaction", false);
            String creditType = bundle.getString("creditType");

            if (!isValidPaymentType(paymentType)) {
                throw new IllegalArgumentException("Invalid payment details: paymentType");
            }
            if (amount == null) {
                throw new IllegalArgumentException("Invalid payment details: amount");
            }
            if (callerId == null) {
                throw new IllegalArgumentException("Invalid payment details: callerId");
            }

            
            Uri.Builder uriBuilder = new Uri.Builder()
                    .scheme("getnet")
                    .authority("pagamento")
                    .appendPath("v3")
                    .appendPath("payment")
                    .appendQueryParameter("paymentType", paymentType)
                    .appendQueryParameter("amount", amount)
                    .appendQueryParameter("callerId", callerId)
                    .appendQueryParameter("orderId", orderId).appendQueryParameter("allowPrintCurrentTransaction", String.valueOf(allowPrintCurrentTransaction));
            

            if (currencyPosition != null) {
                uriBuilder.appendQueryParameter("currencyPosition", currencyPosition);
            }
            if (currencyCode > 0) {
                uriBuilder.appendQueryParameter("currencyCode", String.valueOf(currencyCode));
            }
            
            uriBuilder.appendQueryParameter("allowPrintCurrentTransaction", String.valueOf(allowPrintCurrentTransaction));

            if ("CREDIT".equals(paymentType) && creditType != null && !creditType.isEmpty()) {
                processCreditType(bundle, uriBuilder);
            }

            
            Intent paymentIntent = new Intent(Intent.ACTION_VIEW);
            paymentIntent.setData(uriBuilder.build());
            
            binding.startActivityForResult(paymentIntent, RequestConstants.PAYMENT_REQUEST_CODE);

            Bundle response = new Bundle();
            response.putString("code", "SUCCESS");
            
            return response;
        } catch (IllegalArgumentException e) {
            
            return createErrorBundle(e.getMessage());
        } catch (Exception e) {
            
            return createErrorBundle(e.getMessage() != null ? e.getMessage() : "An unexpected error occurred");
        }
    }

    private boolean isValidPaymentType(String paymentType) {
        return "CREDIT".equals(paymentType) || "DEBIT".equals(paymentType) || "VOUCHER".equals(paymentType) || "PIX".equals(paymentType);
    }

    private void processCreditType(Bundle bundle, Uri.Builder uriBuilder) {
        String creditType = bundle.getString("creditType");
        int installments = bundle.getInt("installments", 0);
        
        

        if (("creditMerchant".equals(creditType) || "creditIssuer".equals(creditType)) && installments <= 1) {
            throw new IllegalArgumentException("Installments cannot be null and must be greater than 1 when paymentType is 'credit' and creditType is 'creditMerchant' or 'creditIssuer'");
        }

        if (creditType != null) {
            uriBuilder.appendQueryParameter("creditType", creditType);
            uriBuilder.appendQueryParameter("installments", String.valueOf(installments));
        }
    }

    private Bundle createErrorBundle(String message) {
        Bundle errorResponse = new Bundle();
        errorResponse.putString("code", "ERROR");
        errorResponse.putString("message", message);
        return errorResponse;
    }
}

