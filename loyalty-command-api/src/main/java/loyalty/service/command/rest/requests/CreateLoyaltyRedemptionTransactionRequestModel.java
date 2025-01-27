package loyalty.service.command.rest.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import loyalty.service.core.validation.NonZeroPoints;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoyaltyRedemptionTransactionRequestModel {

    @NotBlank(message = "loyaltyBankId is a required field")
    private String loyaltyBankId;

    @NotBlank(message = "paymentId is a required field")
    private String paymentId;

    @NonZeroPoints
    private int points;
}
