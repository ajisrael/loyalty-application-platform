package loyalty.service.command.rest.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountUpdatedResponseModel {

    private String accountId;
}
