package loyalty.service.core.constants;

public class LogMessages {

    private LogMessages() {
        throw new IllegalStateException("Constants class");
    }

    public static final String INTERCEPTED_COMMAND = "Intercepted command: %s";

    public static final String SENDING_COMMAND_FOR_ENTITY = "Sending command %s for %s %s";
    public static final String SENDING_COMMAND_FOR_ACCOUNT = String.format(SENDING_COMMAND_FOR_ENTITY, "%s", "account", "%s");
}
