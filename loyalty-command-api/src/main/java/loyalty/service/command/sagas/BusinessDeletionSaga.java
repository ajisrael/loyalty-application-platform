package loyalty.service.command.sagas;

import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.events.BusinessDeletedEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.LoyaltyBankDeletionStartedEvent;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.gateway.EventGateway;
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
import java.util.List;

import static loyalty.service.core.constants.DomainConstants.COMMAND_PROJECTION_GROUP;
import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;

@Saga
@ProcessingGroup(COMMAND_PROJECTION_GROUP)
@Order(2)
public class BusinessDeletionSaga implements Serializable {
    // TODO:
    //  - move strings to constants
    //  - add error handling to log if something went wrong with loyalty bank deletion
    //  - add deadline manager for if something goes wrong with loyalty bank deletion

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient EventGateway eventGateway;
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
                    LoyaltyBankDeletionStartedEvent loyaltyBankDeletionEvent = LoyaltyBankDeletionStartedEvent.builder()
                            .requestId(event.getRequestId())
                            .loyaltyBankId(loyaltyBankLookupEntity.getLoyaltyBankId())
                            .build();

                    LOGGER.info(
                            MarkerGenerator.generateMarker(loyaltyBankDeletionEvent),
                            "Publishing {} loyaltyBankDeletionEvent",
                            loyaltyBankDeletionEvent.getClass().getSimpleName()
                    );

                    eventGateway.publish(loyaltyBankDeletionEvent);
                }
        );
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
