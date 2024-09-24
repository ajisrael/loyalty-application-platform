package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.rest.requests.CreateLoyaltyBankRequestModel;
import loyalty.service.command.rest.responses.LoyaltyBankCreatedResponseModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bank")
@Tag(name = "Loyalty Service Command API")
public class LoyaltyBankCommandController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create loyalty bank")
    public LoyaltyBankCreatedResponseModel createLoyaltyBank(@Valid @RequestBody CreateLoyaltyBankRequestModel requestModel) {

        CreateLoyaltyBankCommand createLoyaltyBankCommand = CreateLoyaltyBankCommand.builder()
                .loyaltyBankId(UUID.randomUUID().toString())
                .accountId(requestModel.getAccountId())
                .businessName(requestModel.getBusinessName())
                .build();

        String loyaltyBankId = commandGateway.sendAndWait(createLoyaltyBankCommand);

        return LoyaltyBankCreatedResponseModel.builder().loyaltyBankId(loyaltyBankId).build();
    }
}
