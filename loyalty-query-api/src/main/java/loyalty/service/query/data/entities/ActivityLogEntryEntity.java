package loyalty.service.query.data.entities;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import loyalty.service.query.data.enums.ActivityLogType;
import loyalty.service.query.data.enums.Actor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@Document(collection = "activity_log_entries")
public class ActivityLogEntryEntity {
    @Id
    private String requestId; // The id of the request from the event that generated this log
    private ActivityLogType activityLogType;
    @Indexed()
    private String activityLogId; // The id of the loyaltyBank, account, or business
    private List<String> messages = new ArrayList<>(); // All messages from changes that occurred from the request
    private Instant timestamp;
    private Actor actor; // The person or system that performed the action for the log
}
