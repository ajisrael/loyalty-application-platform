package loyalty.service.command.commands;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.BUSINESS_ID_CANNOT_BE_EMPTY;
import static loyalty.service.core.constants.ExceptionMessages.BUSINESS_NAME_CANNOT_BE_EMPTY;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class UpdateBusinessCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String businessId;
    private String businessName;

    @Override
    public void validate() {
        super.validate();
        throwExceptionIfParameterIsNullOrBlank(this.getBusinessId(), BUSINESS_ID_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getBusinessName(), BUSINESS_NAME_CANNOT_BE_EMPTY);
    }
}
