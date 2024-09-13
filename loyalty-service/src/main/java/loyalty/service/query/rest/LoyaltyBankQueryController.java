package loyalty.service.query.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import loyalty.service.core.rest.PageResponseType;
import loyalty.service.core.rest.PaginationResponse;
import loyalty.service.core.utils.PaginationUtility;
import loyalty.service.query.queries.FindAccountQuery;
import loyalty.service.query.queries.FindAllAccountsQuery;
import loyalty.service.query.queries.FindLoyaltyBankWithAccountIdQuery;
import loyalty.service.query.queryModels.AccountQueryModel;
import loyalty.service.query.queryModels.LoyaltyBankQueryModel;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE;
import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/bank")
@Tag(name = "Loyalty Bank Query API")
public class LoyaltyBankQueryController {

    @Autowired
    QueryGateway queryGateway;

//    @GetMapping
//    @ResponseBody
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get loyalty banks")
//    public CompletableFuture<PaginationResponse<LoyaltyBankQueryModel>> getLoyaltyBanks(
//            @RequestParam(defaultValue = DEFAULT_PAGE) int currentPage,
//            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
//        FindAllLoyaltyBanksQuery findAllLoyaltyBanksQuery = FindAllLoyaltyBanksQuery.builder()
//                .pageable(PaginationUtility.buildPageable(currentPage, pageSize))
//                .build();
//
//        return queryGateway.query(findAllLoyaltyBanksQuery, new PageResponseType<>(LoyaltyBankQueryModel.class))
//                .thenApply(PaginationUtility::toPageResponse);
//    }

    @GetMapping(params = "accountId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get loyalty bank by account id")
    public CompletableFuture<LoyaltyBankQueryModel> getLoyaltyBankWithAccountId(String accountId) {
        return queryGateway.query(new FindLoyaltyBankWithAccountIdQuery(accountId),
                ResponseTypes.instanceOf(LoyaltyBankQueryModel.class));
    }
}
