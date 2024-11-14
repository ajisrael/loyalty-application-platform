package loyalty.service.command.rest.requests.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RollbackLoyaltyBankCreationCommandRequestModel {

    @NotBlank(message = "requestId is a required field")
    private String requestId;

    @NotBlank(message = "loyaltyBankId is a required field")
    private String loyaltyBankId;
}
