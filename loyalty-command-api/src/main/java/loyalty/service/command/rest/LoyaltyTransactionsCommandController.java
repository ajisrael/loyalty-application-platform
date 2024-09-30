package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.transactions.*;
import loyalty.service.command.rest.requests.CreateLoyaltyRedemptionTransactionRequestModel;
import loyalty.service.command.rest.requests.CreateLoyaltyTransactionRequestModel;
import loyalty.service.command.rest.responses.RedemptionTransactionCreatedResponseModel;
import loyalty.service.command.rest.responses.TransactionCreatedResponseModel;
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
@RequestMapping("/transaction")
@Tag(name = "Loyalty Transactions Command API")
public class LoyaltyTransactionsCommandController {

    @Autowired
    private CommandGateway commandGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyTransactionsCommandController.class);

    @PostMapping("/pending")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create pending transaction")
    public TransactionCreatedResponseModel createPendingTransaction(
            @Valid @RequestBody CreateLoyaltyTransactionRequestModel request) {
        String requestId = UUID.randomUUID().toString();

        CreatePendingTransactionCommand command = CreatePendingTransactionCommand.builder()
                .requestId(requestId)
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_LOYALTY_BANK, command.getClass().getSimpleName(), command.getLoyaltyBankId()
        );

        commandGateway.sendAndWait(command);

        return TransactionCreatedResponseModel.builder().requestId(requestId).build();
    }

    @PostMapping("/earn")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create earned transaction")
    public TransactionCreatedResponseModel createEarnedTransaction(
            @Valid @RequestBody CreateLoyaltyTransactionRequestModel request) {
        String requestId = UUID.randomUUID().toString();

        CreateEarnedTransactionCommand command = CreateEarnedTransactionCommand.builder()
                .requestId(requestId)
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_LOYALTY_BANK, command.getClass().getSimpleName(), command.getLoyaltyBankId()
        );

        commandGateway.sendAndWait(command);

        return TransactionCreatedResponseModel.builder().requestId(requestId).build();
    }

    @PostMapping("/award")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create awarded transaction")
    public TransactionCreatedResponseModel createAwardedTransaction(
            @Valid @RequestBody CreateLoyaltyTransactionRequestModel request) {
        String requestId = UUID.randomUUID().toString();

        CreateAwardedTransactionCommand command = CreateAwardedTransactionCommand.builder()
                .requestId(requestId)
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_LOYALTY_BANK, command.getClass().getSimpleName(), command.getLoyaltyBankId()
        );

        commandGateway.sendAndWait(command);

        return TransactionCreatedResponseModel.builder().requestId(requestId).build();
    }

    @PostMapping("/authorize")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create authorize transaction")
    public RedemptionTransactionCreatedResponseModel createAuthorizeTransaction(
            @Valid @RequestBody CreateLoyaltyTransactionRequestModel request) {
        String requestId = UUID.randomUUID().toString();
        String paymentId = UUID.randomUUID().toString();

        CreateAuthorizedTransactionCommand command = CreateAuthorizedTransactionCommand.builder()
                .requestId(requestId)
                .paymentId(paymentId)
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_LOYALTY_BANK, command.getClass().getSimpleName(), command.getLoyaltyBankId()
        );

        commandGateway.sendAndWait(command);

        return RedemptionTransactionCreatedResponseModel.builder().requestId(requestId).paymentId(paymentId).build();
    }

    @PostMapping("/void")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create void transaction")
    public TransactionCreatedResponseModel createVoidTransaction(
            @Valid @RequestBody CreateLoyaltyRedemptionTransactionRequestModel request) {
        String requestId = UUID.randomUUID().toString();

        CreateVoidTransactionCommand command = CreateVoidTransactionCommand.builder()
                .requestId(requestId)
                .paymentId(request.getPaymentId())
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_LOYALTY_BANK, command.getClass().getSimpleName(), command.getLoyaltyBankId()
        );

        commandGateway.sendAndWait(command);

        return TransactionCreatedResponseModel.builder().requestId(requestId).build();
    }

    @PostMapping("/capture")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create capture transaction")
    public TransactionCreatedResponseModel createCaptureTransaction(
            @Valid @RequestBody CreateLoyaltyRedemptionTransactionRequestModel request) {
        String requestId = UUID.randomUUID().toString();

        CreateCapturedTransactionCommand command = CreateCapturedTransactionCommand.builder()
                .requestId(requestId)
                .paymentId(request.getPaymentId())
                .loyaltyBankId(request.getLoyaltyBankId())
                .points(request.getPoints())
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                SENDING_COMMAND_FOR_LOYALTY_BANK, command.getClass().getSimpleName(), command.getLoyaltyBankId()
        );

        commandGateway.sendAndWait(command);

        return TransactionCreatedResponseModel.builder().requestId(requestId).build();
    }
}