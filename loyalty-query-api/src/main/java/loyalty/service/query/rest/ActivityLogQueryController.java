package loyalty.service.query.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import loyalty.service.core.rest.PageResponseType;
import loyalty.service.core.rest.PaginationResponse;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.core.utils.PaginationUtility;
import loyalty.service.query.queries.FindActivityLogQuery;
import loyalty.service.query.queries.FindAllActivityLogsQuery;
import loyalty.service.query.queryModels.ActivityLogQueryModel;
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
@RequestMapping("/activityLog")
@Tag(name = "ActivityLog Query API")
public class ActivityLogQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogQueryController.class);

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get activityLogs")
    public CompletableFuture<PaginationResponse<ActivityLogQueryModel>> getActivityLogs(
            @RequestParam(defaultValue = DEFAULT_PAGE) int currentPage,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        FindAllActivityLogsQuery query = FindAllActivityLogsQuery.builder()
                .requestId(UUID.randomUUID().toString())
                .pageable(PaginationUtility.buildPageable(currentPage, pageSize))
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, new PageResponseType<>(ActivityLogQueryModel.class))
                .thenApply(PaginationUtility::toPageResponse);
    }

    @GetMapping(params = "activityLogId")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get activityLog by id")
    public CompletableFuture<PaginationResponse<ActivityLogQueryModel>> getActivityLog(String activityLogId) {
        FindActivityLogQuery query = FindActivityLogQuery.builder()
                .requestId(UUID.randomUUID().toString())
                .activityLogId(activityLogId)
                .build();

        LOGGER.info(MarkerGenerator.generateMarker(query), SENDING_QUERY, query.getClass().getSimpleName());

        return queryGateway.query(query, new PageResponseType<>(ActivityLogQueryModel.class))
                .thenApply(PaginationUtility::toPageResponse);
    }
}
