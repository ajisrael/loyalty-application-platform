package loyalty.service.command.commands;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class DeleteAccountCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String accountId;

    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getAccountId(), ACCOUNT_ID_CANNOT_BE_EMPTY);
    }
}
