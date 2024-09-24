package loyalty.service.query.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import loyalty.service.core.rest.PageResponseType;
import loyalty.service.core.rest.PaginationResponse;
import loyalty.service.core.utils.PaginationUtility;
import loyalty.service.query.queries.FindAllLoyaltyBanksQuery;
import loyalty.service.query.queries.FindLoyaltyBankQuery;
import loyalty.service.query.queries.FindLoyaltyBanksWithAccountIdQuery;
import loyalty.service.query.querymodels.LoyaltyBankQueryModel;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE;
import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/bank")
@Tag(name = "Loyalty Bank Query API")
public class LoyaltyBankQueryController {

    @Autowired
    QueryGateway queryGateway;

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get loyalty banks")
    public CompletableFuture<PaginationResponse<LoyaltyBankQueryModel>> getLoyaltyBanks(
            @RequestParam(defaultValue = DEFAULT_PAGE) int currentPage,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        FindAllLoyaltyBanksQuery findAllLoyaltyBanksQuery = FindAllLoyaltyBanksQuery.builder()
                .pageable(PaginationUtility.buildPageable(currentPage, pageSize))
                .build();

        return queryGateway.query(findAllLoyaltyBanksQuery, new PageResponseType<>(LoyaltyBankQueryModel.class))
                .thenApply(PaginationUtility::toPageResponse);
    }

    @GetMapping(params = "accountId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get loyalty banks for account")
    public CompletableFuture<List<LoyaltyBankQueryModel>> getLoyaltyBanksForAccount(String accountId) {
        return queryGateway.query(new FindLoyaltyBanksWithAccountIdQuery(accountId),
                       ResponseTypes.multipleInstancesOf(LoyaltyBankQueryModel.class));
    }

    @GetMapping(params = "loyaltyBankId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get loyalty bank")
    public CompletableFuture<LoyaltyBankQueryModel> getLoyaltyBank(String loyaltyBankId) {
        return queryGateway.query(new FindLoyaltyBankQuery(loyaltyBankId),
                ResponseTypes.instanceOf(LoyaltyBankQueryModel.class));
    }
}
