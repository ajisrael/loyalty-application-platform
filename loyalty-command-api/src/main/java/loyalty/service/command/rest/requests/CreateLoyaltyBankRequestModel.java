package loyalty.service.command.rest.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateLoyaltyBankRequestModel {

    @NotBlank(message = "businessId is a required field")
    private String businessId;
    @NotBlank(message = "accountId is a required field")
    private String accountId;
}
