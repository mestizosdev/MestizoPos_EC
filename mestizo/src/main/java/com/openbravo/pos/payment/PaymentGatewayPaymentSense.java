package com.openbravo.pos.payment;

import uk.co.pos_apps.PosApps;
import uk.co.pos_apps.common.AppContext;

public class PaymentGatewayPaymentSense implements PaymentGateway {

    private static final String PAYMENT_PROCESSOR = "PaymentSense";

    @Override
    public void execute(PaymentInfoMagcard payinfo) {
        AppContext.paymentComplete = false;
        AppContext.paymentResult = null;
        AppContext.P_S_PDQ_STATUS = "Initializing...";

        int timer = 0;
        int timeout = 180;

        PosApps.initPayment(PAYMENT_PROCESSOR, payinfo.getTotal());

        while (!AppContext.paymentComplete) {
            try {
                Thread.sleep(1000);
                timer += 1;
                if (timer > timeout) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (AppContext.paymentResult == null) {
            payinfo.paymentError("Transaction Error! Please try again", "No Response");
        }
        if (AppContext.paymentResult.getTransactionResult().equals("SUCCESSFUL")){
            payinfo.setCardName(AppContext.paymentResult.getCardSchemeName());
            payinfo.setVerification(AppContext.paymentResult.getPaymentMethod());
            payinfo.setChipAndPin(true);
            payinfo.paymentOK(AppContext.paymentResult.getAuthCode(), AppContext.paymentResult.getTransactionId(), AppContext.paymentResult.getTransactionResult());
        }
        else {
            payinfo.paymentError("Transaction Error! Please try again", AppContext.paymentResult.getTransactionResult());
        }
    }
}
