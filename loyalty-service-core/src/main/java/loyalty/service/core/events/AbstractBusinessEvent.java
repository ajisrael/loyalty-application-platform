package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractBusinessEvent extends AbstractEvent {

    private String businessId;
}
