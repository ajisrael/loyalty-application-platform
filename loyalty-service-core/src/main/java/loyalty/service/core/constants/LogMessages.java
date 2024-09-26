package loyalty.service.core.constants;

public class LogMessages {

    private LogMessages() {
        throw new IllegalStateException("Constants class");
    }

    public static final String INTERCEPTED_COMMAND = "Intercepted command: {}";
    public static final String SENDING_COMMAND_FOR_ENTITY = "Sending command %s for %s %s";
    public static final String SENDING_COMMAND_FOR_ACCOUNT = String.format(SENDING_COMMAND_FOR_ENTITY, "%s", "account", "%s");
    public static final String SENDING_COMMAND_FOR_LOYALTY_BANK = String.format(SENDING_COMMAND_FOR_ENTITY, "%s", "loyalty bank", "%s");
    public static final String EMAIL_FOUND_ON_ANOTHER_ACCOUNT_CANCELLING_COMMAND = "Existing email found on account {}. cancelling command {}";
    public static final String ACCOUNT_NOT_FOUND_CANCELLING_COMMAND = "Account {} does not exist, cancelling command {}";
    public static final String ACCOUNT_ALREADY_ENROLLED_IN_BUSINESS_CANCELLING_COMMAND = "Account {} already enrolled in business {}, cancelling command {}";
}
