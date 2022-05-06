package com.openbravo.pos.payment;

import uk.co.pos_apps.PosApps;
import uk.co.pos_apps.payment.dejavoo.DejavooProcessor;

public class PaymentGatewayDejavoo implements PaymentGateway {

    private static final String PAYMENT_PROCESSOR = "Dejavoo";

    @Override
    public void execute(PaymentInfoMagcard payinfo) {
        DejavooProcessor.INSTANCE.setPaymentComplete(false);

        int timer = 0;
        int timeout = 120;

        PosApps.initPayment(PAYMENT_PROCESSOR, payinfo.getTotal());

        while (!DejavooProcessor.INSTANCE.getPaymentComplete()) {
            try {
                Thread.sleep(1000);
                timer += 1;
                if (timer > timeout) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (DejavooProcessor.INSTANCE.getResponse().getSuccess().equals("0")){
            payinfo.setCardName(DejavooProcessor.response.getCardType());
            payinfo.setVerification(DejavooProcessor.response.getVerification());
            payinfo.setChipAndPin(true);
            payinfo.paymentOK(DejavooProcessor.response.getAuthCode(), DejavooProcessor.response.getTransactionId(), DejavooProcessor.response.getMessage());
            payinfo.setLastFourDigits(DejavooProcessor.response.getCardNumber());
        }
        else if (DejavooProcessor.response.getSuccess().equals("1")){
            payinfo.paymentError("Error! Please try again","Device message: "+DejavooProcessor.response.getMessage());
        }
    }
}
