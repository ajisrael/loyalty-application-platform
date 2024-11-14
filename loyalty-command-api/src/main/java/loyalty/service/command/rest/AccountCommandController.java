package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.command.commands.StartAccountAndLoyaltyBankCreationCommand;
import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.command.rest.requests.CreateAccountAndLoyaltyBankRequestModel;
import loyalty.service.command.rest.requests.CreateAccountRequestModel;
import loyalty.service.command.rest.requests.DeleteAccountRequestModel;
import loyalty.service.command.rest.requests.UpdateAccountRequestModel;
import loyalty.service.command.rest.responses.AccountAndLoyaltyBankCreatedResponseModel;
import loyalty.service.command.rest.responses.AccountCreatedResponseModel;
import loyalty.service.core.utils.MarkerGenerator;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static loyalty.service.core.constants.LogMessages.SENDING_COMMAND_FOR_ACCOUNT;
import static loyalty.service.core.constants.LogMessages.SENDING_COMMAND_FOR_ACCOUNT_AND_LOYALTY_BANK;

@RestController
@RequestMapping("/account")
@Tag(name = "Loyalty Service Command API")
public class AccountCommandController {

    @Autowired
    private CommandGateway commandGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCommandController.class);

    @PostMapping(path = "/loyaltyBank")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create account and loyaltyBank")
    public AccountAndLoyaltyBankCreatedResponseModel createAccountAndLoyaltyBank(@Valid @RequestBody CreateAccountAndLoyaltyBankRequestModel request) {
        String accountId = UUID.randomUUID().toString();
        String loyaltyBankId = UUID.randomUUID().toString();
        StartAccountAndLoyaltyBankCreationCommand command = StartAccountAndLoyaltyBankCreationCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .accountId(accountId)
                .loyaltyBankId(loyaltyBankId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .businessId(request.getBusinessId())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_ACCOUNT_AND_LOYALTY_BANK, command.getClass().getSimpleName(), command.getAccountId()
        );

        commandGateway.sendAndWait(command);

        return AccountAndLoyaltyBankCreatedResponseModel.builder()
                .accountId(accountId)
                .loyaltyBankId(loyaltyBankId)
                .build();
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create account")
    public AccountCreatedResponseModel createAccount(@Valid @RequestBody CreateAccountRequestModel request) {
        CreateAccountCommand command = CreateAccountCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .accountId(UUID.randomUUID().toString())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_ACCOUNT, command.getClass().getSimpleName(), command.getAccountId()
        );

        String accountId = commandGateway.sendAndWait(command);

        return AccountCreatedResponseModel.builder().accountId(accountId).build();
    }

    @PutMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update account")
    public void updateAccount(@Valid @RequestBody UpdateAccountRequestModel request) {
        UpdateAccountCommand command = UpdateAccountCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .accountId(request.getAccountId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_ACCOUNT, command.getClass().getSimpleName(), command.getAccountId()
        );

        commandGateway.sendAndWait(command);
    }

    @DeleteMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete account")
    public void deleteAccount(@Valid @RequestBody DeleteAccountRequestModel request) {
        DeleteAccountCommand command = DeleteAccountCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .accountId(request.getAccountId())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_ACCOUNT, command.getClass().getSimpleName(), command.getAccountId()
        );

        commandGateway.sendAndWait(command);
    }
}