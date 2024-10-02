package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.constants.LogMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;

@Component
@AllArgsConstructor
@ProcessingGroup("loyalty-bank-lookup-group")
public class LoyaltyBankLookupEventsHandler {

    private LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankLookupEventsHandler.class);

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(LoyaltyBankCreatedEvent event) {
        loyaltyBankLookupRepository.save(
                new LoyaltyBankLookupEntity(
                        event.getLoyaltyBankId(),
                        event.getAccountId(),
                        event.getBusinessId()
                )
        );

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), LOYALTY_BANK_SAVED_IN_LOOKUP_DB, event.getLoyaltyBankId());
    }

    @EventHandler
    public void on(LoyaltyBankDeletedEvent event) {
        LoyaltyBankLookupEntity loyaltyBankLookupEntity = loyaltyBankLookupRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        throwExceptionIfEntityDoesNotExist(loyaltyBankLookupEntity, String.format(LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, event.getLoyaltyBankId()));
        loyaltyBankLookupRepository.delete(loyaltyBankLookupEntity);

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), LOYALTY_BANK_DELETED_FROM_LOOKUP_DB, event.getLoyaltyBankId());
    }
}
