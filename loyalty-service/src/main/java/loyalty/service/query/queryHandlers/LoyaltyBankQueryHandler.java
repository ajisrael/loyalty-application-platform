package loyalty.service.query.queryHandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.entities.LoyaltyBankEntity;
import loyalty.service.core.data.repositories.LoyaltyBankRepository;
import loyalty.service.core.exceptions.LoyaltyBankWithAccountIdNotFoundException;
import loyalty.service.query.queries.FindLoyaltyBankWithAccountIdQuery;
import loyalty.service.query.queryModels.LoyaltyBankQueryModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoyaltyBankQueryHandler {

    private final LoyaltyBankRepository loyaltyBankRepository;

    @QueryHandler
    public LoyaltyBankQueryModel findLoyaltyBankWithAccountId(FindLoyaltyBankWithAccountIdQuery query) {
        LoyaltyBankEntity loyaltyBankEntity = loyaltyBankRepository.findByAccountId(query.getAccountId()).orElseThrow(
                () -> new LoyaltyBankWithAccountIdNotFoundException(query.getAccountId()));
        return convertLoyaltyBankEntityToLoyaltyBankQueryModel(loyaltyBankEntity);
    }

    private LoyaltyBankQueryModel convertLoyaltyBankEntityToLoyaltyBankQueryModel(LoyaltyBankEntity loyaltyBankEntity) {
        int available =
                loyaltyBankEntity.getEarned() - loyaltyBankEntity.getReserved() - loyaltyBankEntity.getRedeemed();
        return new LoyaltyBankQueryModel(
                loyaltyBankEntity.getLoyaltyBankId(),
                loyaltyBankEntity.getAccountId(),
                loyaltyBankEntity.getPending(),
                loyaltyBankEntity.getEarned(),
                loyaltyBankEntity.getRedeemed(),
                loyaltyBankEntity.getReserved(),
                available
        );
    }
}
