package loyalty.service.command.rest.requests.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RollbackAccountCreationCommandRequestModel {

    @NotBlank(message = "requestId is a required field")
    private String requestId;

    @NotBlank(message = "accountId is a required field")
    private String accountId;
}
