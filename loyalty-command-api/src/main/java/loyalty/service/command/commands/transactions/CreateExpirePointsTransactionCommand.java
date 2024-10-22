package loyalty.service.command.commands.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static loyalty.service.core.constants.ExceptionMessages.TARGET_TRANSACTION_ID_CANNOT_BE_EMPTY;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class CreateExpirePointsTransactionCommand extends AbstractTransactionCommand  {
    private String targetTransactionId;

    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getTargetTransactionId(), TARGET_TRANSACTION_ID_CANNOT_BE_EMPTY);
    }
}
