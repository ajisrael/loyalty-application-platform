package loyalty.service.query.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import loyalty.service.core.rest.PageResponseType;
import loyalty.service.core.rest.PaginationResponse;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.core.utils.PaginationUtility;
import loyalty.service.query.queries.FindAllLoyaltyBanksQuery;
import loyalty.service.query.queries.FindLoyaltyBankQuery;
import loyalty.service.query.queries.FindLoyaltyBanksWithAccountIdQuery;
import loyalty.service.query.queries.FindLoyaltyBanksWithBusinessIdQuery;
import loyalty.service.query.queryModels.LoyaltyBankQueryModel;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE;
import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE_SIZE;
import static loyalty.service.core.constants.LogMessages.SENDING_QUERY;

@RestController
@RequestMapping("/bank")
@Tag(name = "Loyalty Bank Query API")
public class LoyaltyBankQueryController {

    public static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankQueryController.class);
    @Autowired
    private QueryGateway queryGateway;

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get loyalty banks")
    public CompletableFuture<PaginationResponse<LoyaltyBankQueryModel>> getLoyaltyBanks(
            @RequestParam(defaultValue = DEFAULT_PAGE) int currentPage,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        FindAllLoyaltyBanksQuery query = FindAllLoyaltyBanksQuery.builder()
                .requestId(UUID.randomUUID().toString())
                .pageable(PaginationUtility.buildPageable(currentPage, pageSize))
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, new PageResponseType<>(LoyaltyBankQueryModel.class))
                .thenApply(PaginationUtility::toPageResponse);
    }

    // TODO: make this a paginated response
    @GetMapping(params = "accountId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get loyalty banks for account")
    public CompletableFuture<List<LoyaltyBankQueryModel>> getLoyaltyBanksForAccount(String accountId) {
        FindLoyaltyBanksWithAccountIdQuery query = FindLoyaltyBanksWithAccountIdQuery.builder()
                .requestId(UUID.randomUUID().toString())
                .accountId(accountId)
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, ResponseTypes.multipleInstancesOf(LoyaltyBankQueryModel.class));
    }

    // TODO: make this a paginated response
    @GetMapping(params = "businessId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get loyalty banks for business")
    public CompletableFuture<List<LoyaltyBankQueryModel>> getLoyaltyBanksForBusiness(String businessId) {
        FindLoyaltyBanksWithBusinessIdQuery query = FindLoyaltyBanksWithBusinessIdQuery.builder()
                .requestId(UUID.randomUUID().toString())
                .businessId(businessId)
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, ResponseTypes.multipleInstancesOf(LoyaltyBankQueryModel.class));
    }

    @GetMapping(params = "loyaltyBankId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get loyalty bank")
    public CompletableFuture<LoyaltyBankQueryModel> getLoyaltyBank(String loyaltyBankId) {
        FindLoyaltyBankQuery query = FindLoyaltyBankQuery.builder()
                .requestId(UUID.randomUUID().toString())
                .loyaltyBankId(loyaltyBankId)
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, ResponseTypes.instanceOf(LoyaltyBankQueryModel.class));
    }
}
