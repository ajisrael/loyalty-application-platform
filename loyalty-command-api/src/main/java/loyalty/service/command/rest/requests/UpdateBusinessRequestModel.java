package loyalty.service.command.rest.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateBusinessRequestModel {

    @NotBlank(message = "businessId is a required field")
    private String businessId;
    @NotBlank(message = "businessName is a required field")
    private String businessName;
}
