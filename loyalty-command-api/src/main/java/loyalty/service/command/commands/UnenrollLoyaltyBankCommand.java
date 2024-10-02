package loyalty.service.command.commands;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_ID_CANNOT_BE_EMPTY;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class UnenrollLoyaltyBankCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String loyaltyBankId;

    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getLoyaltyBankId(), LOYALTY_BANK_ID_CANNOT_BE_EMPTY);
    }
}
