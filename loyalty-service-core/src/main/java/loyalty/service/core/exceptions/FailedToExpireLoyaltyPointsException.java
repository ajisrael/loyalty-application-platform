package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.FAILED_TO_EXPIRE_LOYALTY_POINTS;

public class FailedToExpireLoyaltyPointsException extends IllegalStateException {
    public FailedToExpireLoyaltyPointsException(String loyaltyBankId) {
        super(String.format(FAILED_TO_EXPIRE_LOYALTY_POINTS, loyaltyBankId));
    }
}
