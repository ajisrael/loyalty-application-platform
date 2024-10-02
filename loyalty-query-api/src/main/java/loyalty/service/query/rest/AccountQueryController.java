package loyalty.service.query.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import loyalty.service.core.rest.PageResponseType;
import loyalty.service.core.rest.PaginationResponse;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.core.utils.PaginationUtility;
import loyalty.service.query.queries.FindAccountQuery;
import loyalty.service.query.queries.FindAllAccountsQuery;
import loyalty.service.query.querymodels.AccountQueryModel;
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
import static loyalty.service.core.constants.LogMessages.SENDING_COMMAND_FOR_ACCOUNT;
import static loyalty.service.core.constants.LogMessages.SENDING_QUERY;

@RestController
@RequestMapping("/account")
@Tag(name = "Account Query API")
public class AccountQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountQueryController.class);

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get accounts")
    public CompletableFuture<PaginationResponse<AccountQueryModel>> getAccounts(
            @RequestParam(defaultValue = DEFAULT_PAGE) int currentPage,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        FindAllAccountsQuery query = FindAllAccountsQuery.builder()
                .pageable(PaginationUtility.buildPageable(currentPage, pageSize))
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, new PageResponseType<>(AccountQueryModel.class))
                .thenApply(PaginationUtility::toPageResponse);
    }

    @GetMapping(params = "accountId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get account by id")
    public CompletableFuture<AccountQueryModel> getAccount(String accountId) {
        FindAccountQuery query = FindAccountQuery.builder()
                .requestId(UUID.randomUUID().toString())
                .accountId(accountId)
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, ResponseTypes.instanceOf(AccountQueryModel.class));
    }
}
