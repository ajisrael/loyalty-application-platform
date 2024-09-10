package loyalty.service.command.rest.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAccountRequestModel {

    @NotBlank(message = "firstName is a required field")
    private String firstName;
    @NotBlank(message = "lastName is a required field")
    private String lastName;
    @NotBlank(message = "email is a required field")
    private String email;

    // TODO: figure out data structure for contract Ids
//    @NotBlank(message = "contractId is a required field")
//    private String contractId;

}
