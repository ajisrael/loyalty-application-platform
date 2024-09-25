package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.EMAIL_ALREADY_EXISTS_FOR_ANOTHER_ACCOUNT;

public class EmailExistsForAccountException extends RuntimeException {
    public EmailExistsForAccountException(String email) {
        super(String.format(EMAIL_ALREADY_EXISTS_FOR_ANOTHER_ACCOUNT, email));
    }
}
