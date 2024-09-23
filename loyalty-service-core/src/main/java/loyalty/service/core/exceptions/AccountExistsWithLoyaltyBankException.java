package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.ACCOUNT_ID_ALREADY_HAS_LOYALTY_BANK;

public class AccountExistsWithLoyaltyBankException extends RuntimeException {
    public AccountExistsWithLoyaltyBankException(String accountId) {
        super(String.format(ACCOUNT_ID_ALREADY_HAS_LOYALTY_BANK, accountId));
    }
}
