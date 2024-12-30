package loyalty.service.query.data.repositories;

import loyalty.service.query.data.entities.ActivityLogEntryEntity;
import loyalty.service.query.data.enums.ActivityLogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ActivityLogRepository extends MongoRepository<ActivityLogEntryEntity, String> {

    Optional<ActivityLogEntryEntity> findByRequestIdAndActivityLogType(String requestId, ActivityLogType activityLogType);
    void deleteAllByActivityLogId(String activityLogId);
    Page<ActivityLogEntryEntity> findByActivityLogId(String activityLogId, Pageable pageable);
}
