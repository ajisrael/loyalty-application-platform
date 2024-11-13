package loyalty.service.command.commands;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class EndAccountAndLoyaltyBankCreationCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String requestId;

    public void validate() {
        throwExceptionIfParameterIsNullOrBlank(this.getRequestId(), REQUEST_ID_CANNOT_BE_EMPTY);
    }
}