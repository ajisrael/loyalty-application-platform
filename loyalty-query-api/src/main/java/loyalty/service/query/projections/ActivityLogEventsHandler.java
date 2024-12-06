package loyalty.service.query.projections;

import org.axonframework.config.ProcessingGroup;
import org.springframework.stereotype.Component;

import static loyalty.service.core.constants.DomainConstants.ACTIVITY_LOG_GROUP;

@Component
@ProcessingGroup(ACTIVITY_LOG_GROUP)
public class ActivityLogEventsHandler {
}
