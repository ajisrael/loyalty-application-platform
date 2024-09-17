package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.transactions.*;
import loyalty.service.command.rest.requests.CreateLoyaltyTransactionRequestModel;
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
            @Valid @RequestBody CreateLoyaltyTransactionRequestModel request) {
        CreatePendingTransactionCommand command = CreatePendingTransactionCommand.builder()
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        commandGateway.sendAndWait(command);

        // TODO: generate request ids for idempotency
    }

    @PostMapping("/earn")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create earned transaction")
    public void createEarnedTransaction(
            @Valid @RequestBody CreateLoyaltyTransactionRequestModel request) {
        CreateEarnedTransactionCommand command = CreateEarnedTransactionCommand.builder()
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        commandGateway.sendAndWait(command);

        // TODO: generate request ids for idempotency
    }

    @PostMapping("/authorize")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create authorize transaction")
    public void createAuthorizeTransaction(
            @Valid @RequestBody CreateLoyaltyTransactionRequestModel request) {
        CreateAuthorizedTransactionCommand command = CreateAuthorizedTransactionCommand.builder()
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        commandGateway.sendAndWait(command);

        // TODO: generate request ids for idempotency
    }

    @PostMapping("/void")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create void transaction")
    public void createVoidTransaction(
            @Valid @RequestBody CreateLoyaltyTransactionRequestModel request) {
        CreateVoidTransactionCommand command = CreateVoidTransactionCommand.builder()
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        commandGateway.sendAndWait(command);

        // TODO: generate request ids for idempotency
    }

    @PostMapping("/capture")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create capture transaction")
    public void createCaptureTransaction(
            @Valid @RequestBody CreateLoyaltyTransactionRequestModel request) {
        CreateCapturedTransactionCommand command = CreateCapturedTransactionCommand.builder()
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        commandGateway.sendAndWait(command);

        // TODO: generate request ids for idempotency
    }
}