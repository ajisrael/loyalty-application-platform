package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE;

public class IllegalLoyaltyBankStateException extends IllegalStateException {
    public IllegalLoyaltyBankStateException(String loyaltyBankProperty) {
        super(String.format(LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE, loyaltyBankProperty));
    }
}
