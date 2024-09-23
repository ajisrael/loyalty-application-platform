package loyalty.service.command.commands;

import lombok.Builder;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@Builder
public class DeleteAccountCommand {

    @TargetAggregateIdentifier
    private String accountId;

    public void validate() {
        throwExceptionIfParameterIsNullOrBlank(this.getAccountId(), ACCOUNT_ID_CANNOT_BE_EMPTY);
    }
}
