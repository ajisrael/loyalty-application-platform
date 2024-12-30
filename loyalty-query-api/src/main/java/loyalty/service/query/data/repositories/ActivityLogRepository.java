package loyalty.service.query.data.repositories;

import loyalty.service.query.data.entities.ActivityLogEntryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ActivityLogRepository extends MongoRepository<ActivityLogEntryEntity, String> {

    Optional<ActivityLogEntryEntity> findByRequestId(String requestId);
    Optional<ActivityLogEntryEntity> findByActivityLogId(String activityLogId);
    void deleteAllByActivityLogId(String activityLogId);
}
