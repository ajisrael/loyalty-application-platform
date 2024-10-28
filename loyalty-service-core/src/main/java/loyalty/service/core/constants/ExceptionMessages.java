package loyalty.service.core.constants;

import static loyalty.service.core.constants.DomainConstants.*;

public class ExceptionMessages {

    private ExceptionMessages() {
        throw new IllegalStateException("Constants class");
    }

    public static final String PARAMETER_CANNOT_BE_EMPTY = "%s cannot be empty";
    public static final String PARAMETER_CANNOT_BE_NULL = "%s cannot be null";
    public static final String ENTITY_WITH_ID_DOES_NOT_EXIST = "%s with id %s does not exist";
    public static final String ENTITY_WITH_ID_ALREADY_EXISTS = "%s with id %s already exists";

    public static final String ACCOUNT_ID_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "accountId");
    public static final String FIRST_NAME_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "firstName");
    public static final String LAST_NAME_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "lastName");
    public static final String EMAIL_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "email");
    public static final String LOYALTY_BANK_ID_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "loyaltyBankId");
    public static final String BUSINESS_ID_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "businessId");
    public static final String BUSINESS_NAME_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "businessName");
    public static final String REQUEST_ID_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "requestId");
    public static final String PAYMENT_ID_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "paymentId");
    public static final String TARGET_TRANSACTION_ID_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "targetTransactionId");
    public static final String POINTS_CANNOT_BE_LTE_ZERO = "points cannot be less than or equal to zero";

    public static final String INVALID_EMAIL_FORMAT = "%s is not a valid email";

    public static final String ACCOUNT_WITH_ID_ALREADY_EXISTS = String.format(ENTITY_WITH_ID_ALREADY_EXISTS, ACCOUNT, "%s");
    public static final String ACCOUNT_WITH_ID_OR_EMAIL_ALREADY_EXISTS = String.format(ENTITY_WITH_ID_ALREADY_EXISTS, ACCOUNT, "%s");
    public static final String ACCOUNT_WITH_ID_DOES_NOT_EXIST = String.format(ENTITY_WITH_ID_DOES_NOT_EXIST, ACCOUNT, "%s");
    public static final String BUSINESS_WITH_ID_DOES_NOT_EXIST = String.format(ENTITY_WITH_ID_DOES_NOT_EXIST, BUSINESS, "%s");
    // TODO: fix wording with id
    public static final String PAYMENT_ID_DOES_NOT_EXIST = String.format(ENTITY_WITH_ID_DOES_NOT_EXIST, PAYMENT_ID, "%s");
    public static final String LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST = String.format(ENTITY_WITH_ID_DOES_NOT_EXIST, LOYALTY_BANK, "%s");
    public static final String THERE_ARE_NO_LOYALTY_BANKS_WITH_ACCOUNT_ID = "There are no loyalty banks with account id %s";
    public static final String THERE_ARE_NO_LOYALTY_BANKS_WITH_BUSINESS_ID = "There are no loyalty banks with businessId %s";

    public static final String EMAIL_ALREADY_EXISTS_FOR_ANOTHER_ACCOUNT = "Email %s already exists for another account";
    public static final String ACCOUNT_ID_ALREADY_HAS_LOYALTY_BANK = "AccountId %s already has a loyalty bank with %s";
    public static final String LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE = "%s balance cannot be negative";
    public static final String FAILED_TO_EXPIRE_LOYALTY_POINTS = "Failed to expire points in loyalty bank %s";

    public static final String CANNOT_VOID_MORE_POINTS_THAN_AVAILABLE = "Cannot void more points than available";
    public static final String CANNOT_CAPTURE_MORE_POINTS_THAN_AVAILABLE = "Cannot capture more points than available";
}
