package loyalty.service.command.sagas;

import loyalty.service.command.commands.DeleteLoyaltyBankCommand;
import loyalty.service.command.commands.ExpireAllPointsCommand;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.events.loyalty.bank.AllPointsExpiredEvent;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.LoyaltyBankDeletionStartedEvent;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.io.Serializable;

import static loyalty.service.core.constants.DomainConstants.COMMAND_PROJECTION_GROUP;
import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;

@Saga
@ProcessingGroup(COMMAND_PROJECTION_GROUP)
@Order(3)
public class LoyaltyBankDeletionSaga implements Serializable {

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankDeletionSaga.class);

    @SagaEventHandler(associationProperty = "loyaltyBankId")
    @StartSaga
    public void handle(LoyaltyBankDeletionStartedEvent event) {
        String loyaltyBankId = event.getLoyaltyBankId();
        SagaLifecycle.associateWith("loyaltyBankId", loyaltyBankId);

        Marker marker = Markers.append(REQUEST_ID, event.getRequestId());
        LOGGER.info(marker, "{} received, expiring points", event.getClass().getSimpleName());

        ExpireAllPointsCommand command = ExpireAllPointsCommand.builder()
                .requestId(event.getRequestId())
                .loyaltyBankId(loyaltyBankId)
                .build();

        LOGGER.info(
                MarkerGenerator.generateMarker(command),
                "Sending {} command",
                command.getClass().getSimpleName()
        );

        commandGateway.send(command);
    }

    @SagaEventHandler(associationProperty = "loyaltyBankId")
    public void handle(AllPointsExpiredEvent event) {
        DeleteLoyaltyBankCommand command = DeleteLoyaltyBankCommand.builder()
                .requestId(event.getRequestId())
                .loyaltyBankId(event.getLoyaltyBankId())
                .build();

        Marker marker = MarkerGenerator.generateMarker(command);
        marker.add(Markers.append(REQUEST_ID, command.getRequestId()));
        LOGGER.info(marker, "{} received, issuing {}", event.getClass().getSimpleName(), command.getClass().getSimpleName());

        commandGateway.send(command);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "loyaltyBankId")
    public void handle(LoyaltyBankDeletedEvent event) {
        Marker marker = Markers.append(REQUEST_ID, event.getRequestId());
        LOGGER.info(marker, "Loyalty bank has been deleted, ending saga");
    }
}
