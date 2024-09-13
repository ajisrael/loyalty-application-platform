package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.*;
import loyalty.service.command.rest.requests.CreatePendingTransactionRequestModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
@Tag(name = "Loyalty Transactions Command API")
public class LoyaltyTransactionsCommandController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping("/pending")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create pending transaction")
    public void createPendingTransaction(
            @Valid @RequestBody CreatePendingTransactionRequestModel request) {
        CreatePendingTransactionCommand createPendingTransactionCommand = CreatePendingTransactionCommand.builder()
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        commandGateway.sendAndWait(createPendingTransactionCommand);

        // TODO: generate request ids for idempotency
    }
}