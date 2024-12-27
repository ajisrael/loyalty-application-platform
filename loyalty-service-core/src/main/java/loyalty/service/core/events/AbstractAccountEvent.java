package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractAccountEvent extends AbstractEvent {
    // TODO: extend all account events from this class

    private String accountId;
}
