package com.andrew.wechetshop.service;

import com.andrew.wechetshop.controller.AuthController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TelVerificationServiceTest {
    public static AuthController.TelAndCode VALID_TEL = new AuthController.TelAndCode("13800000000", null);
    public static AuthController.TelAndCode VALID_TEL_CODE = new AuthController.TelAndCode("13800000000", "000000");
    public static AuthController.TelAndCode EMPTY_TEL = new AuthController.TelAndCode(null, null);

    @Test
    public void returnTrueIfValid() {
        Assertions.assertTrue(new TelVerificationService().isValidTelNumber(VALID_TEL));
    }

    @Test
    public void returnFalseIFInvalid() {
        Assertions.assertFalse(new TelVerificationService().isValidTelNumber(EMPTY_TEL));
        Assertions.assertFalse(new TelVerificationService().isValidTelNumber(null));
    }

}
