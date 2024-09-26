package loyalty.service.command.commands;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class CreateLoyaltyBankCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String loyaltyBankId;
    private String accountId;
    private String businessName;


    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getLoyaltyBankId(), LOYALTY_BANK_ID_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getAccountId(), ACCOUNT_ID_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getBusinessName(), BUSINESS_NAME_CANNOT_BE_EMPTY);
    }
}
