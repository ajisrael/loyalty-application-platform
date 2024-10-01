package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.PAYMENT_ID_DOES_NOT_EXIST;

public class PaymentIdNotFoundException extends RuntimeException {
    public PaymentIdNotFoundException(String paymentId) {
        super(String.format(PAYMENT_ID_DOES_NOT_EXIST, paymentId));
    }
}
