package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.command.projections.AccountLookupEventsHandler;
import loyalty.service.command.rest.requests.CreateAccountRequestModel;
import loyalty.service.command.rest.requests.DeleteAccountRequestModel;
import loyalty.service.command.rest.requests.UpdateAccountRequestModel;
import loyalty.service.command.rest.responses.AccountCreatedResponseModel;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/account")
@Tag(name = "Loyalty Service Command API")
public class AccountCommandController {

    @Autowired
    private CommandGateway commandGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCommandController.class);

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create account")
    public AccountCreatedResponseModel createAccount(@Valid @RequestBody CreateAccountRequestModel createAccountRequestModel) {
        CreateAccountCommand createAccountCommand = CreateAccountCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .accountId(UUID.randomUUID().toString())
                .firstName(createAccountRequestModel.getFirstName())
                .lastName(createAccountRequestModel.getLastName())
                .email(createAccountRequestModel.getEmail())
                .build();

        Marker marker = Markers.append("requestId", createAccountCommand.getRequestId());

        LOGGER.info(marker, String.format("Sending CreateAccountCommand for account %s", createAccountCommand.getAccountId()));

        String accountId = commandGateway.sendAndWait(createAccountCommand);

        return AccountCreatedResponseModel.builder().accountId(accountId).build();
    }

    @PutMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update account")
    public void updateAccount(@Valid @RequestBody UpdateAccountRequestModel updateAccountRequestModel) {
        UpdateAccountCommand updateAccountCommand = UpdateAccountCommand.builder()
                .accountId(updateAccountRequestModel.getAccountId())
                .firstName(updateAccountRequestModel.getFirstName())
                .lastName(updateAccountRequestModel.getLastName())
                .email(updateAccountRequestModel.getEmail())
                .build();

        commandGateway.sendAndWait(updateAccountCommand);
    }

    @DeleteMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete account")
    public void deleteAccount(@Valid @RequestBody DeleteAccountRequestModel deleteAccountRequestModel) {
        DeleteAccountCommand deleteAccountCommand = DeleteAccountCommand.builder()
                .accountId(deleteAccountRequestModel.getAccountId())
                .build();

        commandGateway.sendAndWait(deleteAccountCommand);
    }
}