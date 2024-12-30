package loyalty.service.query.queryHandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.exceptions.ActivityLogNotFoundException;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.query.data.entities.ActivityLogEntryEntity;
import loyalty.service.query.data.entities.ActivityLogEntryEntity;
import loyalty.service.query.data.repositories.ActivityLogRepository;
import loyalty.service.query.queries.FindActivityLogQuery;
import loyalty.service.query.queries.FindAllActivityLogsQuery;
import loyalty.service.query.queryModels.ActivityLogQueryModel;
import net.logstash.logback.marker.Markers;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;

@Component
@AllArgsConstructor
public class ActivityLogQueryHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogQueryHandler.class);
    private final ActivityLogRepository activityLogRepository;

    @QueryHandler
    public Page<ActivityLogQueryModel> findAllActivityLogs(FindAllActivityLogsQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        return activityLogRepository.findAll(query.getPageable())
                .map(this::convertActivityLogEntryEntityToActivityLogQueryModel);
    }

    @QueryHandler
    public Page<ActivityLogQueryModel> findActivityLog(FindActivityLogQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        String activityLogId = query.getActivityLogId();

        Page<ActivityLogEntryEntity> activityLogEntryEntities = activityLogRepository.findByActivityLogId(activityLogId, query.getPageable());

        if (activityLogEntryEntities.isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), ACTIVITY_LOG_NOT_FOUND_IN_DB, activityLogId);
            throw new ActivityLogNotFoundException(activityLogId);
        }

        return activityLogEntryEntities.map(this::convertActivityLogEntryEntityToActivityLogQueryModel);
    }

    private ActivityLogQueryModel convertActivityLogEntryEntityToActivityLogQueryModel(ActivityLogEntryEntity activityLogEntryEntity) {
        return new ActivityLogQueryModel(
                activityLogEntryEntity.getRequestId(),
                activityLogEntryEntity.getActivityLogId(),
                activityLogEntryEntity.getMessages(),
                activityLogEntryEntity.getTimestamp(),
                activityLogEntryEntity.getActor()
        );
    }
}
