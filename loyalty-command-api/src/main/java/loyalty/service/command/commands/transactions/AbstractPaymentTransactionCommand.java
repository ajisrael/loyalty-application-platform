package loyalty.service.command.commands.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public abstract class AbstractPaymentTransactionCommand extends AbstractTransactionCommand {
    private String paymentId;

    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getPaymentId(), PAYMENT_ID_CANNOT_BE_EMPTY);
    }

}
