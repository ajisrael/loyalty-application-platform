package loyalty.service.query.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import loyalty.service.core.rest.PageResponseType;
import loyalty.service.core.rest.PaginationResponse;
import loyalty.service.core.utils.PaginationUtility;
import loyalty.service.query.queries.FindAccountQuery;
import loyalty.service.query.queries.FindAllAccountsQuery;
import loyalty.service.query.querymodels.AccountQueryModel;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE;
import static loyalty.service.core.constants.DomainConstants.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/account")
@Tag(name = "Account Query API")
public class AccountQueryController {

    @Autowired
    QueryGateway queryGateway;

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get accounts")
    public CompletableFuture<PaginationResponse<AccountQueryModel>> getAccounts(
            @RequestParam(defaultValue = DEFAULT_PAGE) int currentPage,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        FindAllAccountsQuery findAllAccountsQuery = FindAllAccountsQuery.builder()
                .pageable(PaginationUtility.buildPageable(currentPage, pageSize))
                .build();

        return queryGateway.query(findAllAccountsQuery, new PageResponseType<>(AccountQueryModel.class))
                .thenApply(PaginationUtility::toPageResponse);
    }

    @GetMapping(params = "accountId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get account by id")
    public CompletableFuture<AccountQueryModel> getAccount(String accountId) {
        return queryGateway.query(new FindAccountQuery(accountId),
                ResponseTypes.instanceOf(AccountQueryModel.class));
    }
}