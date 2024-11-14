package loyalty.service.command.commands.rollbacks;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.command.commands.AbstractCommand;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_ID_CANNOT_BE_EMPTY;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class RollbackLoyaltyBankCreationCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String loyaltyBankId;

    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getLoyaltyBankId(), LOYALTY_BANK_ID_CANNOT_BE_EMPTY);
    }}
