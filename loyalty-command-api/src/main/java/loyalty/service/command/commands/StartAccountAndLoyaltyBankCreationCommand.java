package loyalty.service.command.commands;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfEmailIsInvalid;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class StartAccountAndLoyaltyBankCreationCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String requestId;

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;

    private String loyaltyBankId;
    private String businessId;

    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getAccountId(), ACCOUNT_ID_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getFirstName(), FIRST_NAME_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getLastName(), LAST_NAME_CANNOT_BE_EMPTY);
        throwExceptionIfEmailIsInvalid(this.getEmail());
        throwExceptionIfParameterIsNullOrBlank(this.getLoyaltyBankId(), LOYALTY_BANK_ID_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getBusinessId(), BUSINESS_ID_CANNOT_BE_EMPTY);
    }
}
