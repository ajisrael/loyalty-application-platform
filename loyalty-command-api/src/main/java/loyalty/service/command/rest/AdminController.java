package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.EndAccountAndLoyaltyBankCreationCommand;
import loyalty.service.command.commands.rollbacks.RollbackAccountCreationCommand;
import loyalty.service.command.commands.rollbacks.RollbackLoyaltyBankCreationCommand;
import loyalty.service.command.rest.requests.admin.EndAccountAndLoyaltyBankCreationCommandRequestModel;
import loyalty.service.command.rest.requests.admin.RollbackAccountCreationCommandRequestModel;
import loyalty.service.command.rest.requests.admin.RollbackLoyaltyBankCreationCommandRequestModel;
import loyalty.service.core.utils.MarkerGenerator;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static loyalty.service.core.constants.LogMessages.SENDING_COMMAND_FOR_REQUEST;

@RestController
@RequestMapping("/admin")
@Tag(name = "Loyalty Service Command API")
public class AdminController {

    @Autowired
    private CommandGateway commandGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @PostMapping(path = "/EndAccountAndLoyaltyBankCreationCommand")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Issues EndAccountAndLoyaltyBankCreationCommand")
    public void sendEndAccountAndLoyaltyBankCreationCommand(
            @Valid @RequestBody EndAccountAndLoyaltyBankCreationCommandRequestModel request) {
        EndAccountAndLoyaltyBankCreationCommand command = EndAccountAndLoyaltyBankCreationCommand.builder()
                .requestId(request.getRequestId())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_REQUEST, command.getClass().getSimpleName(), command.getRequestId()
        );

        commandGateway.sendAndWait(command);
    }

    @PostMapping(path = "/RollbackAccountCreationCommand")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Issues RollbackAccountCreationCommand")
    public void sendRollbackAccountCreationCommand(
            @Valid @RequestBody RollbackAccountCreationCommandRequestModel request) {
        RollbackAccountCreationCommand command = RollbackAccountCreationCommand.builder()
                .requestId(request.getRequestId())
                .accountId(request.getAccountId())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_REQUEST, command.getClass().getSimpleName(), command.getRequestId()
        );

        commandGateway.sendAndWait(command);
    }

    @PostMapping(path = "/RollbackLoyaltyBankCreationCommand")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Issues RollbackLoyaltyBankCreationCommand")
    public void sendRollbackLoyaltyBankCreationCommand(
            @Valid @RequestBody RollbackLoyaltyBankCreationCommandRequestModel request) {
        RollbackLoyaltyBankCreationCommand command = RollbackLoyaltyBankCreationCommand.builder()
                .requestId(request.getRequestId())
                .loyaltyBankId(request.getLoyaltyBankId())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_REQUEST, command.getClass().getSimpleName(), command.getRequestId()
        );

        commandGateway.sendAndWait(command);
    }
}
