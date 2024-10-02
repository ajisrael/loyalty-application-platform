package loyalty.service.query.queryHandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.exceptions.BusinessNotFoundException;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.query.data.entities.BusinessEntity;
import loyalty.service.query.data.repositories.BusinessRepository;
import loyalty.service.query.queries.FindAllBusinessesQuery;
import loyalty.service.query.queries.FindBusinessQuery;
import loyalty.service.query.queryModels.BusinessQueryModel;
import net.logstash.logback.marker.Markers;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.BUSINESS_NOT_FOUND_IN_DB;
import static loyalty.service.core.constants.LogMessages.PROCESSING_QUERY;

@Component
@AllArgsConstructor
public class BusinessQueryHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(BusinessQueryHandler.class);
    private final BusinessRepository businessRepository;

    @QueryHandler
    public Page<BusinessQueryModel> findAllBusinesses(FindAllBusinessesQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        return businessRepository.findAll(query.getPageable())
                .map(this::convertBusinessEntityToBusinessQueryModel);
    }

    @QueryHandler
    public BusinessQueryModel findBusiness(FindBusinessQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        String businessId = query.getBusinessId();

        Optional<BusinessEntity> businessEntityOptional = businessRepository.findById(businessId);

        if (businessEntityOptional.isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), BUSINESS_NOT_FOUND_IN_DB, businessId);
            throw new BusinessNotFoundException(businessId);
        }

        return convertBusinessEntityToBusinessQueryModel(businessEntityOptional.get());
    }

    private BusinessQueryModel convertBusinessEntityToBusinessQueryModel(BusinessEntity businessEntity) {
        return new BusinessQueryModel(
                businessEntity.getBusinessId(),
                businessEntity.getBusinessName()
        );
    }
}
