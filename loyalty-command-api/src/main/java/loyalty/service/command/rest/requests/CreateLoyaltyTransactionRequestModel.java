package loyalty.service.command.rest.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.core.validation.NonZeroPoints;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoyaltyTransactionRequestModel {

    @NotBlank(message = "loyaltyBankId is a required field")
    private String loyaltyBankId;

    @NonZeroPoints
    private int points;
}
