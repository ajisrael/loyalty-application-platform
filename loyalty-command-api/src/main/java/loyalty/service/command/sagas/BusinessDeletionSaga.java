package loyalty.service.command.sagas;

import loyalty.service.command.commands.DeleteLoyaltyBankCommand;
import loyalty.service.command.commands.ExpireAllPointsCommand;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.events.BusinessDeletedEvent;
import loyalty.service.core.events.AllPointsExpiredEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;

@Saga
public class BusinessDeletionSaga implements Serializable {
    // TODO: move strings to constants

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessDeletionSaga.class);

    @SagaEventHandler(associationProperty = "businessId")
    @StartSaga
    public void handle(BusinessDeletedEvent event) {
        String businessId = event.getBusinessId();
        SagaLifecycle.associateWith("businessId", businessId);

        Marker marker = Markers.append(REQUEST_ID, event.getRequestId());
        LOGGER.info(marker, "{} received, checking for loyaltyBankEntities", event.getClass().getSimpleName());

        List<LoyaltyBankLookupEntity> loyaltyBankLookupEntities = loyaltyBankLookupRepository.findByBusinessId(businessId);

        if (loyaltyBankLookupEntities.isEmpty()) {
            LOGGER.info(marker, "No loyaltyBankEntities found, ending saga");
            SagaLifecycle.end();
            return;
        }

        loyaltyBankLookupEntities.forEach(
                loyaltyBankLookupEntity -> {
                    ExpireAllPointsCommand command = ExpireAllPointsCommand.builder()
                            .requestId(event.getRequestId())
                            .loyaltyBankId(loyaltyBankLookupEntity.getLoyaltyBankId())
                            .build();

                    LOGGER.info(
                            MarkerGenerator.generateMarker(command),
                            "Sending {} command",
                            command.getClass().getSimpleName()
                    );

                    commandGateway.send(command);
                }
        );
    }

    @SagaEventHandler(associationProperty = "businessId")
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

    @SagaEventHandler(associationProperty = "businessId")
    public void handle(LoyaltyBankDeletedEvent event) {
        Marker marker = Markers.append(REQUEST_ID, event.getRequestId());
        LOGGER.info(marker, "{} received, checking for loyaltyBankEntities", event.getClass().getSimpleName());

        List<LoyaltyBankLookupEntity> loyaltyBankLookupEntities = loyaltyBankLookupRepository.findByBusinessId(event.getBusinessId());

        if (loyaltyBankLookupEntities.isEmpty()) {
            LOGGER.info(marker, "No loyaltyBankEntities found, ending saga");
            SagaLifecycle.end();
            return;
        }

        LOGGER.info(
                marker, "{} loyaltyBankEntities remaining, continuing saga lifecycle",
                loyaltyBankLookupEntities.size()
        );
    }
}
