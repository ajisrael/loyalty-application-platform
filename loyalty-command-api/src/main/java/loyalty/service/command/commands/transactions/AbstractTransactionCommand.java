package loyalty.service.command.commands.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.command.commands.AbstractCommand;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_ID_CANNOT_BE_EMPTY;
import static loyalty.service.core.constants.ExceptionMessages.POINTS_CANNOT_BE_LTE_ZERO;
import static loyalty.service.core.utils.Helper.*;

@Getter
@SuperBuilder
public abstract class AbstractTransactionCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String loyaltyBankId;
    private int points;

    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getLoyaltyBankId(), LOYALTY_BANK_ID_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNegativeOrZero(this.getPoints(), POINTS_CANNOT_BE_LTE_ZERO);
    }
}
