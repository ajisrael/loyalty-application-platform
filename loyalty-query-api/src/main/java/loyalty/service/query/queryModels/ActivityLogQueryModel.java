package loyalty.service.query.queryModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import loyalty.service.query.data.enums.Actor;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class ActivityLogQueryModel {

    private String requestId;
    private String activityLogId; // The id of the loyaltyBank, account, or business
    private List<String> messages;
    private Instant timestamp;
    private Actor actor;
}
