package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.THERE_ARE_NO_LOYALTY_BANKS_WITH_ACCOUNT_ID;

public class NoLoyaltyBanksForAccountFoundException extends RuntimeException {
    public NoLoyaltyBanksForAccountFoundException(String accountId) {
        super(String.format(THERE_ARE_NO_LOYALTY_BANKS_WITH_ACCOUNT_ID, accountId));
    }
}
