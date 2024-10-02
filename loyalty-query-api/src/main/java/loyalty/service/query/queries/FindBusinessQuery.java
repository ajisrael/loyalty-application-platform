package loyalty.service.query.queries;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindBusinessQuery extends AbstractQuery {

    private String businessId;
}
