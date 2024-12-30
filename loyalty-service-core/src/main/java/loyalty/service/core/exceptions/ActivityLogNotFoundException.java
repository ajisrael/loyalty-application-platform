package loyalty.service.core.exceptions;

import static loyalty.service.core.constants.ExceptionMessages.ACTIVITY_LOG_WITH_ID_DOES_NOT_EXIST;

public class ActivityLogNotFoundException extends RuntimeException {
    public ActivityLogNotFoundException(String accountId) {
        super(String.format(ACTIVITY_LOG_WITH_ID_DOES_NOT_EXIST, accountId));
    }
}
