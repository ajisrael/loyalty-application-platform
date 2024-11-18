package loyalty.service.core.constants;

public class LogMessages {

    private LogMessages() {
        throw new IllegalStateException("Constants class");
    }

    public static final String INTERCEPTED_COMMAND = "Intercepted command: {}";
    public static final String PROCESSING_QUERY = "Processing {}";
    public static final String SENDING_COMMAND_FOR_ENTITY = "Sending command %s for %s %s";
    public static final String SENDING_COMMAND_FOR_ACCOUNT = String.format(SENDING_COMMAND_FOR_ENTITY, "{}", "account", "{}");
    public static final String SENDING_COMMAND_FOR_ACCOUNT_AND_LOYALTY_BANK = String.format(SENDING_COMMAND_FOR_ENTITY, "{}", "account and loyalty bank", "{}");
    public static final String SENDING_COMMAND_FOR_REQUEST = String.format(SENDING_COMMAND_FOR_ENTITY, "{}", "request", "{}");
    public static final String SENDING_COMMAND_FOR_LOYALTY_BANK = String.format(SENDING_COMMAND_FOR_ENTITY, "{}", "loyalty bank", "{}");
    public static final String SENDING_COMMAND_FOR_BUSINESS = String.format(SENDING_COMMAND_FOR_ENTITY, "{}", "business", "{}");

    public static final String PUBLISHING_EVENT_FOR_ENTITY = "Publishing event %s for %s %s";
    public static final String PUBLISHING_EVENT_FOR_REQUEST = String.format(PUBLISHING_EVENT_FOR_ENTITY, "{}", "request", "{}");
    public static final String PUBLISHING_EVENT_FOR_ACCOUNT = String.format(PUBLISHING_EVENT_FOR_ENTITY, "{}", "account", "{}");
    public static final String PUBLISHING_EVENT_FOR_LOYALTY_BANK = String.format(SENDING_COMMAND_FOR_ENTITY, "{}", "loyalty bank", "{}");

    public static final String EMAIL_FOUND_ON_ANOTHER_ACCOUNT_CANCELLING_COMMAND = "Existing email found on account {}. cancelling command {}";
    public static final String ACCOUNT_NOT_FOUND_CANCELLING_COMMAND = "Account {} does not exist, cancelling command {}";
    public static final String PAYMENT_ID_NOT_FOUND_CANCELLING_COMMAND = "Payment id {} does not exist, cancelling command {}";
    public static final String BUSINESS_NOT_FOUND_CANCELLING_COMMAND = "Business {} does not exist, cancelling command {}";
    public static final String ACCOUNT_ALREADY_ENROLLED_IN_BUSINESS_CANCELLING_COMMAND = "Account {} already enrolled in business {}, cancelling command {}";
    public static final String VALIDATING_COMMAND = "Validating command {}";
    public static final String EXCESSIVE_POINTS_REQUEST_CANCELLING_COMMAND = "Attempting to {} more points than authorized, cancelling command {}";

    public static final String INSUFFICIENT_AVAILABLE_POINTS_FOR_AUTHORIZATION = "Insufficient available points for authorization of {} points";

    public static final String ACCOUNT_SAVED_IN_LOOKUP_DB = "Account {} saved in lookup db";
    public static final String ACCOUNT_UPDATED_IN_LOOKUP_DB = "Account {} updated in lookup db";
    public static final String ACCOUNT_DELETED_FROM_LOOKUP_DB = "Account {} deleted from lookup db";
    public static final String LOYALTY_BANK_SAVED_IN_LOOKUP_DB = "Loyalty bank {} saved in lookup db";
    public static final String LOYALTY_BANK_DELETED_FROM_LOOKUP_DB = "Loyalty bank {} deleted from lookup db";
    public static final String BUSINESS_SAVED_IN_LOOKUP_DB = "Business {} saved in lookup db";
    public static final String BUSINESS_UPDATED_IN_LOOKUP_DB = "Business {} updated in lookup db";
    public static final String BUSINESS_DELETED_FROM_LOOKUP_DB = "Business {} deleted from lookup db";

    public static final String EXPIRATION_TRACKER_CREATED_FOR_LOYALTY_BANK = "Expiration tracker created for loyalty bank {}";
    public static final String TRANSACTION_ENTITY_CREATED_FOR_LOYALTY_BANK = "TransactionEntity created for loyalty bank {}";

    public static final String ACCOUNT_SAVED_IN_DB = "Account {} saved in db";
    public static final String ACCOUNT_UPDATED_IN_DB = "Account {} updated in db";
    public static final String ACCOUNT_DELETED_FROM_DB = "Account {} deleted from db";
    public static final String ACCOUNT_NOT_FOUND_IN_DB = "Account {} not found in db";

    public static final String LOYALTY_BANK_SAVED_IN_DB = "Loyalty bank {} saved in db";
    public static final String LOYALTY_BANK_DELETED_FROM_DB = "Loyalty bank {} deleted from db";
    public static final String LOYALTY_BANK_NOT_FOUND_IN_DB = "Loyalty bank {} not found in db";
    public static final String PROCESSED_EVENT_FOR_LOYALTY_BANK = "Processed {} event for loyaltyBank {}";
    public static final String NO_LOYALTY_BANK_FOUND_FOR_ACCOUNT = "No loyalty bank found for account {}";
    public static final String NO_LOYALTY_BANK_FOUND_FOR_BUSINESS = "No loyalty bank found for business {}";

    public static final String BUSINESS_SAVED_IN_DB = "Business {} saved in db";
    public static final String BUSINESS_UPDATED_IN_DB = "Business {} updated in db";
    public static final String BUSINESS_DELETED_FROM_DB = "Business {} deleted from db";
    public static final String BUSINESS_NOT_FOUND_IN_DB = "Business {} not found in db";

    public static final String ACCOUNT_TO_LOYALTY_BANK_SAVED_IN_DB = "Account to loyalty bank relationship for accountId {} saved in db";
    public static final String ACCOUNT_TO_LOYALTY_BANK_DELETED_FROM_DB = "Account to loyalty bank relationship for accountId {} deleted from db";
    public static final String ACCOUNT_TO_LOYALTY_BANK_NOT_FOUND_IN_DB = "Account to loyalty bank relationship for accountId {} not found in db";
    public static final String LOYALTY_BANK_ADDED_TO_ACCOUNT_TO_LOYALTY_BANK_IN_DB = "Loyalty bank {} added to account to loyalty bank relationship for accountId {} in db";
    public static final String LOYALTY_BANK_REMOVED_FROM_ACCOUNT_TO_LOYALTY_BANK_IN_DB = "Loyalty bank {} removed from account to loyalty bank relationship for accountId {} in db";

    public static final String SENDING_QUERY = "Sending query {}";
}
