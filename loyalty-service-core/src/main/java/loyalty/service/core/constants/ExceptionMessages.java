package loyalty.service.core.constants;

import static loyalty.service.core.constants.DomainConstants.ACCOUNT;
import static loyalty.service.core.constants.DomainConstants.LOYALTY_BANK;

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
    public static final String BUSINESS_NAME_CANNOT_BE_EMPTY = String.format(PARAMETER_CANNOT_BE_EMPTY, "businessName");


    public static final String ACCOUNT_WITH_ID_ALREADY_EXISTS = String.format(ENTITY_WITH_ID_ALREADY_EXISTS, ACCOUNT, "%s");
    public static final String ACCOUNT_WITH_ID_OR_EMAIL_ALREADY_EXISTS = String.format(ENTITY_WITH_ID_ALREADY_EXISTS, ACCOUNT, "%s");
    public static final String ACCOUNT_WITH_ID_DOES_NOT_EXIST = String.format(ENTITY_WITH_ID_DOES_NOT_EXIST, ACCOUNT, "%s");
    public static final String LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST = String.format(ENTITY_WITH_ID_DOES_NOT_EXIST, LOYALTY_BANK, "%s");
    public static final String LOYALTY_BANK_WITH_ACCOUNT_ID_DOES_NOT_EXIST = String.format("%s with account id %s does not exist", LOYALTY_BANK, "%s");

    public static final String EMAIL_ALREADY_EXISTS_FOR_AN_ACCOUNT = "Email %s already exists for an account";
    public static final String ACCOUNT_ID_ALREADY_HAS_LOYALTY_BANK = "AccountId %s already has a loyalty bank with %s";
    public static final String LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE = "%s balance cannot be negative";
}
