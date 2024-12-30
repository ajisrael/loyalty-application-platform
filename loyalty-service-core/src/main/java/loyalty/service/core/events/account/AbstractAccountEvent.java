package loyalty.service.core.events.account;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.AbstractEvent;

@Getter
@SuperBuilder
public abstract class AbstractAccountEvent extends AbstractEvent {
    // TODO: extend all account events from this class

    private String accountId;
}
