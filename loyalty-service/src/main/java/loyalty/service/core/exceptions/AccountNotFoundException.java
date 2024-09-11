package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.ACCOUNT_WITH_ID_DOES_NOT_EXIST;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountId) {
        super(String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, accountId));
    }
}
