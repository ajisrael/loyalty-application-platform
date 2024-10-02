package loyalty.service.command.rest.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteLoyaltyBankRequestModel {

    @NotBlank(message = "loyaltyBankId is a required field")
    private String loyaltyBankId;
}
