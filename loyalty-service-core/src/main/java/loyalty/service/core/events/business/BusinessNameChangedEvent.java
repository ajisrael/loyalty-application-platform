package loyalty.service.core.events.business;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BusinessNameChangedEvent extends AbstractBusinessEvent {

    private String oldBusinessName;
    private String newBusinessName;
}
