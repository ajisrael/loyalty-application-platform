package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BusinessDeletedEvent extends AbstractEvent {

    private String businessId;
}
