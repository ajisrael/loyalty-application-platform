package loyalty.service.core.events.business;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BusinessEnrolledEvent extends AbstractBusinessEvent {

    private String businessName;
}
