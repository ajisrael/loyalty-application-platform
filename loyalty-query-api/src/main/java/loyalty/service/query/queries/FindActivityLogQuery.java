package loyalty.service.query.queries;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindActivityLogQuery extends PageableQuery {

    private String activityLogId;
}