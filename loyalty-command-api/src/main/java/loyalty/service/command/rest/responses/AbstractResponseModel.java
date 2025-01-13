package loyalty.service.command.rest.responses;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractResponseModel {
    private String requestId;
}
