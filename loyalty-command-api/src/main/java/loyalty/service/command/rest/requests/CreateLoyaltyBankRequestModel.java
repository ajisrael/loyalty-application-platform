package loyalty.service.command.rest.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateLoyaltyBankRequestModel {

    @NotBlank(message = "businessName is a required field")
    private String businessName;
    @NotBlank(message = "accountId is a required field")
    private String accountId;
}
