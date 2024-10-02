package loyalty.service.query.queries;


import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractQuery {

    private String requestId;
}
