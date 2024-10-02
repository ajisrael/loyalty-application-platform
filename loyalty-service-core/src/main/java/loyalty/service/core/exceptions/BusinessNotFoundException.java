package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.BUSINESS_WITH_ID_DOES_NOT_EXIST;

public class BusinessNotFoundException extends RuntimeException {
    public BusinessNotFoundException(String accountId) {
        super(String.format(BUSINESS_WITH_ID_DOES_NOT_EXIST, accountId));
    }
}
