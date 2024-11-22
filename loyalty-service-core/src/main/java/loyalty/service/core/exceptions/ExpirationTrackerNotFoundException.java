package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.EXPIRATION_TRACKER_FOR_LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST;

public class ExpirationTrackerNotFoundException extends RuntimeException {
    public ExpirationTrackerNotFoundException(String loyaltyBankId) {
        super(String.format(EXPIRATION_TRACKER_FOR_LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, loyaltyBankId));
    }
}
