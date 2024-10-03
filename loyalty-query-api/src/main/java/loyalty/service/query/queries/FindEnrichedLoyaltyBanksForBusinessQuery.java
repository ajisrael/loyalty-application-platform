package loyalty.service.query.queries;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindEnrichedLoyaltyBanksForBusinessQuery extends AbstractQuery {

    private String businessId;
}
