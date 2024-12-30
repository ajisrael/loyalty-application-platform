package loyalty.service.query.data.repositories;

import loyalty.service.query.data.entities.ActivityLogEntryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityLogRepository extends MongoRepository<ActivityLogEntryEntity, String> {

    Optional<ActivityLogEntryEntity> findByRequestId(String requestId);
    Optional<List<ActivityLogEntryEntity>> findByActivityLogId(String activityLogId);
    void deleteAllByActivityLogId(String activityLogId);
    Page<ActivityLogEntryEntity> findByActivityLogId(String activityLogId, Pageable pageable);
}
