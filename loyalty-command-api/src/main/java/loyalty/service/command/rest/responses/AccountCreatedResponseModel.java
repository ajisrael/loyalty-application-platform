package loyalty.service.command.rest.responses;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountCreatedResponseModel extends AbstractResponseModel {

    private String accountId;
}
