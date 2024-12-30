package loyalty.service.core.events.business;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.AbstractEvent;

@Getter
@SuperBuilder
public abstract class AbstractBusinessEvent extends AbstractEvent {

    private String businessId;
}
