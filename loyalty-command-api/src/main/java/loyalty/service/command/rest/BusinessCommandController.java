package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.DeleteBusinessCommand;
import loyalty.service.command.commands.EnrollBusinessCommand;
import loyalty.service.command.commands.UpdateBusinessCommand;
import loyalty.service.command.rest.requests.DeleteBusinessRequestModel;
import loyalty.service.command.rest.requests.EnrollBusinessRequestModel;
import loyalty.service.command.rest.requests.UpdateBusinessRequestModel;
import loyalty.service.command.rest.responses.BusinessEnrolledResponseModel;
import loyalty.service.core.utils.MarkerGenerator;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static loyalty.service.core.constants.LogMessages.SENDING_COMMAND_FOR_BUSINESS;

@RestController
@RequestMapping("/business")
@Tag(name = "Loyalty Service Command API")
public class BusinessCommandController {

    @Autowired
    private CommandGateway commandGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessCommandController.class);

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create business")
    public BusinessEnrolledResponseModel createBusiness(@Valid @RequestBody EnrollBusinessRequestModel request) {
        EnrollBusinessCommand command = EnrollBusinessCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .businessId(UUID.randomUUID().toString())
                .businessName(request.getBusinessName())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_BUSINESS, command.getClass().getSimpleName(), command.getBusinessId()
        );

        String businessId = commandGateway.sendAndWait(command);

        return BusinessEnrolledResponseModel.builder().businessId(businessId).build();
    }

    @PutMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update business")
    public void updateBusiness(@Valid @RequestBody UpdateBusinessRequestModel request) {
        UpdateBusinessCommand command = UpdateBusinessCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .businessId(request.getBusinessId())
                .businessName(request.getBusinessName())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_BUSINESS, command.getClass().getSimpleName(), command.getBusinessId()
        );

        commandGateway.sendAndWait(command);
    }

    @DeleteMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete business")
    public void deleteBusiness(@Valid @RequestBody DeleteBusinessRequestModel request) {
        DeleteBusinessCommand command = DeleteBusinessCommand.builder()
                .requestId(UUID.randomUUID().toString())
                .businessId(request.getBusinessId())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_BUSINESS, command.getClass().getSimpleName(), command.getBusinessId()
        );

        commandGateway.sendAndWait(command);
    }
}