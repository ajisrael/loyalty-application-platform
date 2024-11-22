package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.TRANSACTION_NOT_FOUND;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException() {
        super(TRANSACTION_NOT_FOUND);
    }
}
