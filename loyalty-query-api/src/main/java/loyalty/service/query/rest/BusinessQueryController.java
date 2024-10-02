package loyalty.service.query.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import loyalty.service.core.rest.PageResponseType;
import loyalty.service.core.rest.PaginationResponse;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.core.utils.PaginationUtility;
import loyalty.service.query.queries.FindAllBusinessesQuery;
import loyalty.service.query.queries.FindBusinessQuery;
import loyalty.service.query.queryModels.BusinessQueryModel;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE;
import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE_SIZE;
import static loyalty.service.core.constants.LogMessages.SENDING_QUERY;

@RestController
@RequestMapping("/business")
@Tag(name = "Business Query API")
public class BusinessQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessQueryController.class);

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get businesses")
    public CompletableFuture<PaginationResponse<BusinessQueryModel>> getBusinesss(
            @RequestParam(defaultValue = DEFAULT_PAGE) int currentPage,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        FindAllBusinessesQuery query = FindAllBusinessesQuery.builder()
                .requestId(UUID.randomUUID().toString())
                .pageable(PaginationUtility.buildPageable(currentPage, pageSize))
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, new PageResponseType<>(BusinessQueryModel.class))
                .thenApply(PaginationUtility::toPageResponse);
    }

    @GetMapping(params = "businessId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get business by id")
    public CompletableFuture<BusinessQueryModel> getBusiness(String businessId) {
        FindBusinessQuery query = FindBusinessQuery.builder()
                .requestId(UUID.randomUUID().toString())
                .businessId(businessId)
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, ResponseTypes.instanceOf(BusinessQueryModel.class));
    }
}
