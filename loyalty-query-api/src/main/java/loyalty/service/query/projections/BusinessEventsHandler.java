package loyalty.service.query.projections;

import loyalty.service.core.events.business.BusinessDeletedEvent;
import loyalty.service.core.events.business.BusinessEnrolledEvent;
import loyalty.service.core.events.business.BusinessNameChangedEvent;
import loyalty.service.core.exceptions.BusinessNotFoundException;
import loyalty.service.query.data.entities.BusinessEntity;
import loyalty.service.query.data.repositories.BusinessRepository;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static loyalty.service.core.constants.DomainConstants.BUSINESS_GROUP;
import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;

@Component
@ProcessingGroup(BUSINESS_GROUP)
public class BusinessEventsHandler {
    // TODO: add a projection for the total amount of loyalty points available for this business,
    //  i.e. the amount of points they need to be able to pay out to users

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessEventsHandler.class);

    private final BusinessRepository businessRepository;

    public BusinessEventsHandler(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

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
        BusinessEntity businessEntity = new BusinessEntity();
        BeanUtils.copyProperties(event, businessEntity);
        businessRepository.save(businessEntity);

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), BUSINESS_SAVED_IN_DB, event.getBusinessId());
    }

    @EventHandler
    public void on(BusinessNameChangedEvent event) {
        Optional<BusinessEntity> businessEntityOptional = businessRepository.findByBusinessId(event.getBusinessId());

        if (businessEntityOptional.isPresent()) {
            BusinessEntity businessEntity = businessEntityOptional.get();
            BeanUtils.copyProperties(event, businessEntity);
            businessRepository.save(businessEntity);

            LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), BUSINESS_UPDATED_IN_DB, event.getBusinessId());
        } else {
            LOGGER.error(Markers.append(REQUEST_ID, event.getRequestId()), BUSINESS_NOT_FOUND_IN_DB, event.getBusinessId());
            throw new BusinessNotFoundException(event.getBusinessId());
        }
    }

    @EventHandler
    public void on(BusinessDeletedEvent event) {
        Optional<BusinessEntity> businessEntityOptional = businessRepository.findByBusinessId(event.getBusinessId());

        if (businessEntityOptional.isPresent()) {
            businessRepository.delete(businessEntityOptional.get());
            LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), BUSINESS_DELETED_FROM_DB, event.getBusinessId());
        } else {
            LOGGER.error(Markers.append(REQUEST_ID, event.getRequestId()), BUSINESS_NOT_FOUND_IN_DB, event.getBusinessId());
            throw new BusinessNotFoundException(event.getBusinessId());
        }
    }
}
