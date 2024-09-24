package loyalty.service.command.commands;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsNullOrBlank;

@Getter
@SuperBuilder
public class CreateAccountCommand extends AbstractCommand {

    @TargetAggregateIdentifier
    private String accountId;
    private String firstName;
    private String lastName;
    private String email;

    // TODO: figure out contract id datastructure
    // private String contractId;

    public void validate() {
        throwExceptionIfParameterIsNullOrBlank(this.getAccountId(), ACCOUNT_ID_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getFirstName(), FIRST_NAME_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getLastName(), LAST_NAME_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsNullOrBlank(this.getEmail(), EMAIL_CANNOT_BE_EMPTY);
    }
}
