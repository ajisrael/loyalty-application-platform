package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.repositories.BusinessLookupRepository;
import loyalty.service.core.events.BusinessDeletedEvent;
import loyalty.service.core.events.BusinessEnrolledEvent;
import loyalty.service.core.events.BusinessUpdatedEvent;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.ExceptionMessages.BUSINESS_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.constants.LogMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;

@Component
@AllArgsConstructor
@ProcessingGroup("business-lookup-group")
public class BusinessLookupEventsHandler {

    private BusinessLookupRepository businessLookupRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessLookupEventsHandler.class);

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(BusinessEnrolledEvent event) {
        businessLookupRepository.save(new BusinessLookupEntity(event.getBusinessId()));
        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), BUSINESS_SAVED_IN_LOOKUP_DB, event.getBusinessId());
    }

    @EventHandler
    public void on(BusinessUpdatedEvent event) {
        BusinessLookupEntity businessLookupEntity = businessLookupRepository.findByBusinessId(event.getBusinessId());
        throwExceptionIfEntityDoesNotExist(businessLookupEntity, String.format(BUSINESS_WITH_ID_DOES_NOT_EXIST, event.getBusinessId()));
        BeanUtils.copyProperties(event, businessLookupEntity);
        businessLookupRepository.save(businessLookupEntity);

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), BUSINESS_UPDATED_IN_LOOKUP_DB, event.getBusinessId());
    }

    @EventHandler
    public void on(BusinessDeletedEvent event) {
        BusinessLookupEntity businessLookupEntity = businessLookupRepository.findByBusinessId(event.getBusinessId());
        throwExceptionIfEntityDoesNotExist(businessLookupEntity, String.format(BUSINESS_WITH_ID_DOES_NOT_EXIST, event.getBusinessId()));
        businessLookupRepository.delete(businessLookupEntity);

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), BUSINESS_DELETED_FROM_LOOKUP_DB, event.getBusinessId());
    }
}
