package br.com.jylabtech.getnet_payment_tech.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Map;

public class ActivityResultRulesTest {

    @Test
    public void resultOkConstantMatchesAndroid() {
        assertEquals(android.app.Activity.RESULT_OK, ActivityResultRules.RESULT_OK);
    }

    @Test
    public void pluginRequestCodesAreRecognised() {
        assertTrue(ActivityResultRules.isPluginRequestCode(RequestConstants.PAYMENT_REQUEST_CODE));
        assertTrue(ActivityResultRules.isPluginRequestCode(RequestConstants.STATUS_REQUEST_CODE));
        assertTrue(ActivityResultRules.isPluginRequestCode(RequestConstants.PRE_AUTHORIZE_REQUEST_CODE));
        assertTrue(ActivityResultRules.isPluginRequestCode(RequestConstants.REFUND_REQUEST_CODE));
        assertTrue(ActivityResultRules.isPluginRequestCode(RequestConstants.REPRINT_REQUEST_CODE));
        assertTrue(ActivityResultRules.isPluginRequestCode(RequestConstants.INFO_REQUEST_CODE));
    }

    @Test
    public void otherRequestCodesAreNotConsumed() {
        assertFalse(ActivityResultRules.isPluginRequestCode(0));
        assertFalse(ActivityResultRules.isPluginRequestCode(1));
        assertFalse(ActivityResultRules.isPluginRequestCode(10000));
        assertFalse(ActivityResultRules.isPluginRequestCode(10007));
        assertFalse(ActivityResultRules.isPluginRequestCode(-1));
    }

    @Test
    public void canceledResultCompletesWithCanceledError() {
        Map<String, Object> response = ActivityResultRules.notOkResponse(0); // RESULT_CANCELED

        assertEquals("CANCELED", response.get("code"));
        assertTrue(((String) response.get("message")).contains("cancelada"));
        assertTrue(((String) response.get("message")).contains("resultCode=0"));
    }

    @Test
    public void anyNonOkResultCodeIsCanceled() {
        assertEquals("CANCELED", ActivityResultRules.notOkResponse(1).get("code"));
        assertEquals("CANCELED", ActivityResultRules.notOkResponse(42).get("code"));
    }

    @Test
    public void resultOkHasNoCanceledResponse() {
        assertNull(ActivityResultRules.notOkResponse(ActivityResultRules.RESULT_OK));
    }
}
