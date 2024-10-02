package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.commands.UnenrollLoyaltyBankCommand;
import loyalty.service.command.rest.requests.CreateLoyaltyBankRequestModel;
import loyalty.service.command.rest.requests.DeleteLoyaltyBankRequestModel;
import loyalty.service.command.rest.responses.LoyaltyBankCreatedResponseModel;
import loyalty.service.core.utils.MarkerGenerator;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static loyalty.service.core.constants.LogMessages.SENDING_COMMAND_FOR_LOYALTY_BANK;

@RestController
@RequestMapping("/bank")
@Tag(name = "Loyalty Service Command API")
public class LoyaltyBankCommandController {

    @Autowired
    private CommandGateway commandGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankCommandController.class);

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create loyalty bank")
    public LoyaltyBankCreatedResponseModel createLoyaltyBank(@Valid @RequestBody CreateLoyaltyBankRequestModel request) {

        CreateLoyaltyBankCommand command = CreateLoyaltyBankCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .loyaltyBankId(UUID.randomUUID().toString())
                .accountId(request.getAccountId())
                .businessId(request.getBusinessId())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_LOYALTY_BANK, command.getClass().getSimpleName(), command.getLoyaltyBankId()
        );

        String loyaltyBankId = commandGateway.sendAndWait(command);

        return LoyaltyBankCreatedResponseModel.builder().loyaltyBankId(loyaltyBankId).build();
    }

    @DeleteMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete loyalty bank")
    public void deleteLoyaltyBank(@Valid @RequestBody DeleteLoyaltyBankRequestModel request) {

        UnenrollLoyaltyBankCommand command = UnenrollLoyaltyBankCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .loyaltyBankId(request.getLoyaltyBankId())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_LOYALTY_BANK, command.getClass().getSimpleName(), command.getLoyaltyBankId()
        );

        commandGateway.sendAndWait(command);
    }
}
