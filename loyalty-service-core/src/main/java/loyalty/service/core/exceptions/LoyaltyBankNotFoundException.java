package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST;

public class LoyaltyBankNotFoundException extends RuntimeException {
    public LoyaltyBankNotFoundException(String accountId) {
        super(String.format(LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, accountId));
    }
}
