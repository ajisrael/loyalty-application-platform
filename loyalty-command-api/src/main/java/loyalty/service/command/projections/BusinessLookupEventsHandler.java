package loyalty.service.command.projections;

import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.repositories.BusinessLookupRepository;
import loyalty.service.core.events.business.BusinessDeletedEvent;
import loyalty.service.core.events.business.BusinessEnrolledEvent;
import loyalty.service.core.events.business.BusinessNameChangedEvent;
import loyalty.service.core.exceptions.IllegalProjectionStateException;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.annotation.Validated;

import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.ExceptionMessages.BUSINESS_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.constants.LogMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;

@Component
@Validated
public class BusinessLookupEventsHandler {

    private final BusinessLookupRepository businessLookupRepository;
    private final SmartValidator validator;
    private Marker marker = null;

    public BusinessLookupEventsHandler(BusinessLookupRepository businessLookupRepository, SmartValidator validator) {
        this.businessLookupRepository = businessLookupRepository;
        this.validator = validator;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessLookupEventsHandler.class);

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(marker, exception.getLocalizedMessage());
    }

    @ExceptionHandler(resultType = IllegalProjectionStateException.class)
    public void handle(IllegalProjectionStateException exception) {
        LOGGER.error(marker, exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(BusinessEnrolledEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        BusinessLookupEntity businessLookupEntity = new BusinessLookupEntity(event.getBusinessId());

        marker.add(MarkerGenerator.generateMarker(businessLookupEntity));

        validateEntity(businessLookupEntity);
        businessLookupRepository.save(businessLookupEntity);

        LOGGER.info(marker, BUSINESS_SAVED_IN_LOOKUP_DB, event.getBusinessId());
    }

    @EventHandler
    public void on(BusinessNameChangedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        BusinessLookupEntity businessLookupEntity = businessLookupRepository.findByBusinessId(event.getBusinessId());
        throwExceptionIfEntityDoesNotExist(businessLookupEntity, String.format(BUSINESS_WITH_ID_DOES_NOT_EXIST, event.getBusinessId()));
        BeanUtils.copyProperties(event, businessLookupEntity);

        marker.add(MarkerGenerator.generateMarker(businessLookupEntity));

        validateEntity(businessLookupEntity);
        businessLookupRepository.save(businessLookupEntity);

        LOGGER.info(marker, BUSINESS_UPDATED_IN_LOOKUP_DB, event.getBusinessId());
    }

    @EventHandler
    public void on(BusinessDeletedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        BusinessLookupEntity businessLookupEntity = businessLookupRepository.findByBusinessId(event.getBusinessId());
        throwExceptionIfEntityDoesNotExist(businessLookupEntity, String.format(BUSINESS_WITH_ID_DOES_NOT_EXIST, event.getBusinessId()));

        marker.add(MarkerGenerator.generateMarker(businessLookupEntity));

        businessLookupRepository.delete(businessLookupEntity);

        LOGGER.info(marker, BUSINESS_DELETED_FROM_LOOKUP_DB, event.getBusinessId());
    }

    private void validateEntity(BusinessLookupEntity entity) {
        BindingResult bindingResult = new BeanPropertyBindingResult(entity, "businessLookupEntity");
        validator.validate(entity, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new IllegalProjectionStateException(bindingResult.getFieldError().getDefaultMessage());
        }
    }
}
