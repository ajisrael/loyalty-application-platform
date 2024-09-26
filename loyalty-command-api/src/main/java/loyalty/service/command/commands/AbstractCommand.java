package loyalty.service.command.commands;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static loyalty.service.core.constants.ExceptionMessages.REQUEST_ID_CANNOT_BE_EMPTY;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public abstract class AbstractCommand {
    private String requestId;

    public void validate() {
        throwExceptionIfParameterIsNullOrBlank(this.getRequestId(), REQUEST_ID_CANNOT_BE_EMPTY);
    }
}
