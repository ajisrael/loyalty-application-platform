package loyalty.service.command.rest.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePendingTransactionRequestModel {

    @NotBlank(message = "loyaltyBankId is a required field")
    private String loyaltyBankId;

    @Min(value = 1, message = "points must be a positive number")
    private int points;
}
