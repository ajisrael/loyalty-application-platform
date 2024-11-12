package loyalty.service.command.sagas;

import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.commands.EndAccountAndLoyaltyBankCreationCommand;
import loyalty.service.command.utils.LogHelper;
import loyalty.service.core.events.AccountAndLoyaltyBankCreationEndedEvent;
import loyalty.service.core.events.AccountAndLoyaltyBankCreationStartedEvent;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.utils.MarkerGenerator;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@ProcessingGroup("account-and-loyalty-bank-creation-saga-group")
public class AccountAndLoyaltyBankCreationSaga {


    @Autowired
    private transient CommandGateway commandGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountAndLoyaltyBankCreationSaga.class);

    private String loyaltyBankId = null;
    private String businessId = null;


    @StartSaga
    @SagaEventHandler(associationProperty = "requestId")
    public void handle(AccountAndLoyaltyBankCreationStartedEvent event) {
        CreateAccountCommand command = CreateAccountCommand.builder()
                .requestId(event.getRequestId())
                .accountId(event.getAccountId())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .email(event.getEmail())
                .build();

        loyaltyBankId = event.getLoyaltyBankId();
        businessId = event.getBusinessId();

        LogHelper.logEventIssuingCommand(LOGGER, event, command);

        commandGateway.sendAndWait(command);
    }

    @SagaEventHandler(associationProperty = "requestId")
    public void handle(AccountCreatedEvent event) {
        CreateLoyaltyBankCommand command = CreateLoyaltyBankCommand.builder()
                .requestId(event.getRequestId())
                .loyaltyBankId(loyaltyBankId)
                .businessId(businessId)
                .accountId(event.getAccountId())
                .build();

        LogHelper.logEventIssuingCommand(LOGGER, event, command);

        commandGateway.sendAndWait(command);
    }

    @SagaEventHandler(associationProperty = "requestId")
    public void handle(LoyaltyBankCreatedEvent event) {
        EndAccountAndLoyaltyBankCreationCommand command = EndAccountAndLoyaltyBankCreationCommand.builder()
                .requestId(event.getRequestId())
                .build();

        LogHelper.logEventIssuingCommand(LOGGER, event, command);

        commandGateway.sendAndWait(command);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "requestId")
    public void handle(AccountAndLoyaltyBankCreationEndedEvent event) {
        LOGGER.info(MarkerGenerator.generateMarker(event), "{} event received, ending saga", event.getClass().getSimpleName());
    }
}
