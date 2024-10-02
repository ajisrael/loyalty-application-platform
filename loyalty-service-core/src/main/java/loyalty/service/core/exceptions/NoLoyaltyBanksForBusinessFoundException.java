package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.THERE_ARE_NO_LOYALTY_BANKS_WITH_BUSINESS_ID;

public class NoLoyaltyBanksForBusinessFoundException extends RuntimeException {
    public NoLoyaltyBanksForBusinessFoundException(String businessId) {
        super(String.format(THERE_ARE_NO_LOYALTY_BANKS_WITH_BUSINESS_ID, businessId));
    }
}
