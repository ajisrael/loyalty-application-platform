package loyalty.service.command.commands;

import lombok.Builder;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import static loyalty.service.core.constants.ExceptionMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfParameterIsEmpty;

@Getter
@Builder
public class CreateAccountCommand {

    @TargetAggregateIdentifier
    private String accountId;
    private String firstName;
    private String lastName;
    private String email;

    // TODO: figure out contract id datastructure
    // private String contractId;

    public void validate() {
        throwExceptionIfParameterIsEmpty(this.getAccountId(), ACCOUNT_ID_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsEmpty(this.getFirstName(), FIRST_NAME_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsEmpty(this.getLastName(), LAST_NAME_CANNOT_BE_EMPTY);
        throwExceptionIfParameterIsEmpty(this.getEmail(), EMAIL_CANNOT_BE_EMPTY);
    }
}
