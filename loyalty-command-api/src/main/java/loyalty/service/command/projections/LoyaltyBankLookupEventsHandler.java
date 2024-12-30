package loyalty.service.command.projections;

import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankDeletedEvent;
import loyalty.service.core.exceptions.IllegalProjectionStateException;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import static loyalty.service.core.constants.DomainConstants.COMMAND_PROJECTION_GROUP;
import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.constants.LogMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;

@Component
@ProcessingGroup(COMMAND_PROJECTION_GROUP)
@Order(1)
public class LoyaltyBankLookupEventsHandler {

    private final LoyaltyBankLookupRepository loyaltyBankLookupRepository;
    private final SmartValidator validator;
    private Marker marker = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankLookupEventsHandler.class);

    public LoyaltyBankLookupEventsHandler(LoyaltyBankLookupRepository loyaltyBankLookupRepository, SmartValidator validator) {
        this.loyaltyBankLookupRepository = loyaltyBankLookupRepository;
        this.validator = validator;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(marker, exception.getLocalizedMessage());
    }

    @ExceptionHandler(resultType = IllegalProjectionStateException.class)
    public void handle(IllegalProjectionStateException exception) {
        LOGGER.error(marker, exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(LoyaltyBankCreatedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        LoyaltyBankLookupEntity loyaltyBankLookupEntity = new LoyaltyBankLookupEntity(
                        event.getLoyaltyBankId(),
                        event.getAccountId(),
                        event.getBusinessId()
                );

        marker.add(MarkerGenerator.generateMarker(loyaltyBankLookupEntity));

        validateEntity(loyaltyBankLookupEntity);
        loyaltyBankLookupRepository.save(loyaltyBankLookupEntity);

        LOGGER.info(marker, LOYALTY_BANK_SAVED_IN_LOOKUP_DB, event.getLoyaltyBankId());
    }

    @EventHandler
    public void on(LoyaltyBankDeletedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        LoyaltyBankLookupEntity loyaltyBankLookupEntity = loyaltyBankLookupRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        throwExceptionIfEntityDoesNotExist(loyaltyBankLookupEntity, String.format(LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, event.getLoyaltyBankId()));

        marker.add(MarkerGenerator.generateMarker(loyaltyBankLookupEntity));

        loyaltyBankLookupRepository.delete(loyaltyBankLookupEntity);

        LOGGER.info(marker, LOYALTY_BANK_DELETED_FROM_LOOKUP_DB, event.getLoyaltyBankId());
    }

    private void validateEntity(LoyaltyBankLookupEntity entity) {
        BindingResult bindingResult = new BeanPropertyBindingResult(entity, "loyaltyBankLookupEntity");
        validator.validate(entity, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new IllegalProjectionStateException(bindingResult.getFieldError().getDefaultMessage());
        }
    }
}
