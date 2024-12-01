package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.TRANSACTION_NOT_FOUND;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String transactionId, String loyaltyBankId) {
        super(String.format(TRANSACTION_NOT_FOUND, transactionId, loyaltyBankId));
    }
}
