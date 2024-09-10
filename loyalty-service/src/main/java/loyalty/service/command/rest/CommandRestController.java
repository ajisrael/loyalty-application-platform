package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.rest.requests.CreateAccountRequestModel;
import loyalty.service.command.rest.responses.AccountCreatedResponseModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("")
@Tag(name = "Loyalty Service Command API")
public class CommandRestController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping("/account")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create account")
    public AccountCreatedResponseModel createAccount(@Valid @RequestBody CreateAccountRequestModel createAccountRequestModel) {
        CreateAccountCommand createAccountCommand = CreateAccountCommand.builder()
                .accountId(UUID.randomUUID().toString())
                .firstName(createAccountRequestModel.getFirstName())
                .lastName(createAccountRequestModel.getLastName())
                .email(createAccountRequestModel.getEmail())
                .build();

        String accountId = commandGateway.sendAndWait(createAccountCommand);

        return AccountCreatedResponseModel.builder().accountId(accountId).build();
    }
}


