package loyalty.service.query.queries;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindAccountQuery extends AbstractQuery {

    private String accountId;
}
