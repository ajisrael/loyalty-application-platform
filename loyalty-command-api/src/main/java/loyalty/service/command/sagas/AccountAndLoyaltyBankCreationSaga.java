package loyalty.service.command.sagas;

import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.commands.rollbacks.RollbackAccountCreationCommand;
import loyalty.service.command.commands.rollbacks.RollbackLoyaltyBankCreationCommand;
import loyalty.service.command.utils.LogHelper;
import loyalty.service.core.events.*;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;

@Saga
@ProcessingGroup("account-lookup-group")
@Order(2)
public class AccountAndLoyaltyBankCreationSaga {


    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient EventGateway eventGateway;

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

        try {
            commandGateway.send(command);
        } catch (Exception e) {
            Marker marker = MarkerGenerator.generateMarker(command);
            marker.add(Markers.append("exceptionMessage", e.getLocalizedMessage()));
            LOGGER.error(marker, "CreateAccountCommand failed for account {}", command.getAccountId());
            issueEventToEndSagaOnError(command.getRequestId());

        }
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

        try {
            commandGateway.send(command);
        } catch (Exception e) {
                Marker marker = MarkerGenerator.generateMarker(command);
                marker.add(Markers.append("exceptionMessage", e.getLocalizedMessage()));
                LOGGER.error(marker, "CreateLoyaltyBankCommand failed for account {} and loyaltyBank {}", command.getAccountId(), command.getLoyaltyBankId());
                rollbackAccountCreation(event.getRequestId(), event.getAccountId());
        }
    }

    @SagaEventHandler(associationProperty = "requestId")
    public void handle(LoyaltyBankCreatedEvent event) {
        Marker marker = Markers.append(REQUEST_ID, event.getRequestId());

        AccountAndLoyaltyBankCreationEndedEvent endEvent = AccountAndLoyaltyBankCreationEndedEvent.builder()
                .requestId(event.getRequestId())
                .build();

        LOGGER.info(marker, "{} received, issuing {}", event.getClass().getSimpleName(), endEvent.getClass().getSimpleName());

        try {
            eventGateway.publish(endEvent);
        } catch (Exception e) {
            marker.add(Markers.append("exceptionMessage", e.getLocalizedMessage()));
            LOGGER.error(marker, "AccountAndLoyaltyBankCreationEndedEvent failed to process, manual cleanup of saga required");
        }
    }

    @SagaEventHandler(associationProperty = "requestId")
    public void handle(LoyaltyBankDeletedEvent event) {
       rollbackAccountCreation(event.getRequestId(), event.getAccountId());
    }

    @SagaEventHandler(associationProperty = "requestId")
    public void handle(AccountDeletedEvent event) {
        issueEventToEndSagaOnError(event.getRequestId());
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "requestId")
    public void handle(AccountAndLoyaltyBankCreationEndedEvent event) {
        LOGGER.info(MarkerGenerator.generateMarker(event), "{} event received, ending saga", event.getClass().getSimpleName());
    }

    private void rollbackAccountCreation(String requestId, String accountId) {
        RollbackAccountCreationCommand command = RollbackAccountCreationCommand.builder()
                .requestId(requestId)
                .accountId(accountId)
                .build();

        try {
            commandGateway.send(command);
        } catch (Exception e) {
            Marker marker = Markers.append(REQUEST_ID, command.getRequestId());
            marker.add(Markers.append("exceptionMessage", e.getLocalizedMessage()));
            LOGGER.error(marker, "RollbackAccountCreationCommand failed to process, manual cleanup of saga and aggregate required");
        }
    }

    private void rollbackLoyaltyBankCreation(String requestId, String loyaltyBankId) {
        RollbackLoyaltyBankCreationCommand command = RollbackLoyaltyBankCreationCommand.builder()
                .requestId(requestId)
                .loyaltyBankId(loyaltyBankId)
                .build();

        try {
            commandGateway.send(command);
        } catch (Exception e) {
            Marker marker = MarkerGenerator.generateMarker(command);
            marker.add(Markers.append("exceptionMessage", e.getLocalizedMessage()));
            LOGGER.error(marker, "RollbackLoyaltyBankCreationCommand failed to process, manual cleanup of saga and aggregate required");
        }
    }

    private void issueEventToEndSagaOnError(String requestId) {
        Marker marker = Markers.append(REQUEST_ID, requestId);
        LOGGER.info(marker, "Attempting to end saga due to error");

        AccountAndLoyaltyBankCreationEndedEvent event = AccountAndLoyaltyBankCreationEndedEvent.builder()
                .requestId(requestId)
                .build();

        try {
            eventGateway.publish(event);
        } catch (Exception e) {
            marker.add(Markers.append("exceptionMessage", e.getLocalizedMessage()));
            LOGGER.error(marker, "AccountAndLoyaltyBankCreationEndedEvent failed to process, manual cleanup of saga and aggregate required");
        }
    }
}
