package loyalty.service.core.constants;

import static loyalty.service.core.constants.DomainConstants.ACCOUNT;

public class ExceptionMessages {

    private ExceptionMessages() {
        throw new IllegalStateException("Constants class");
    }

    public static final String PARAMETER_CANNOT_BE_EMPTY = "%s cannot be empty";
    public static final String PARAMETER_CANNOT_BE_NULL = "%s cannot be null";
    public static final String PARAMETER_CANNOT_BE_NEGATIVE = "%s cannot be negative";
    public static final String ENTITY_WITH_ID_DOES_NOT_EXIST = "%s with id %s does not exist";
    public static final String ENTITY_WITH_ID_ALREADY_EXISTS = "%s with id %s already exists";
    public static final String ENTITY_ALREADY_EXISTS_FOR_USER = "%s already exists for user";

    public static final String ACCOUNT_ID_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "accountId");
    public static final String FIRST_NAME_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "firstName");
    public static final String LAST_NAME_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "lastName");
    public static final String EMAIL_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "email");
    public static final String PRICE_CANNOT_BE_NULL = String.format(PARAMETER_CANNOT_BE_NULL, "price");

    public static final String ACCOUNT_WITH_ID_ALREADY_EXISTS = String.format(ENTITY_WITH_ID_ALREADY_EXISTS, ACCOUNT, "%s");
    public static final String ACCOUNT_WITH_ID_DOES_NOT_EXIST = String.format(ENTITY_WITH_ID_DOES_NOT_EXIST, ACCOUNT, "%s");
    public static final String ACCOUNT_ALREADY_CONFIGURED_IN_CATALOG = "Account already configured in catalog";

    public static final String ACCOUNT_WITH_ID_CANNOT_BE_REMOVED_WHILE_ITEMS_IN_STOCK = "Account with id %s cannot be removed. %d items in stock.";
    public static final String FAILED_TO_DECREMENT_ACCOUNT_STOCK_CANNOT_BE_NEGATIVE =
            "Failed to decrement account [%s], stock cannot be negative";
}
