package loyalty.service.command.commands.rollbacks;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.command.commands.AbstractCommand;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.ACCOUNT_ID_CANNOT_BE_EMPTY;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class RollbackAccountCreationCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String accountId;

    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getAccountId(), ACCOUNT_ID_CANNOT_BE_EMPTY);
    }
}
