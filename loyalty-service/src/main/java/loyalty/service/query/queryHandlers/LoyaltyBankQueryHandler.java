package loyalty.service.query.queryHandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.entities.LoyaltyBankEntity;
import loyalty.service.core.data.repositories.LoyaltyBankRepository;
import loyalty.service.core.exceptions.LoyaltyBankWithAccountIdNotFoundException;
import loyalty.service.query.queries.FindAllAccountsQuery;
import loyalty.service.query.queries.FindAllLoyaltyBanksQuery;
import loyalty.service.query.queries.FindLoyaltyBankWithAccountIdQuery;
import loyalty.service.query.queryModels.LoyaltyBankQueryModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoyaltyBankQueryHandler {

    private final LoyaltyBankRepository loyaltyBankRepository;

    @QueryHandler
    public Page<LoyaltyBankQueryModel> findAllAccounts(FindAllLoyaltyBanksQuery query) {
        return loyaltyBankRepository.findAll(query.getPageable())
                .map(this::convertLoyaltyBankEntityToLoyaltyBankQueryModel);
    }

    @QueryHandler
    public LoyaltyBankQueryModel findLoyaltyBankWithAccountId(FindLoyaltyBankWithAccountIdQuery query) {
        LoyaltyBankEntity loyaltyBankEntity = loyaltyBankRepository.findByAccountId(query.getAccountId()).orElseThrow(
                () -> new LoyaltyBankWithAccountIdNotFoundException(query.getAccountId()));
        return convertLoyaltyBankEntityToLoyaltyBankQueryModel(loyaltyBankEntity);
    }

    private LoyaltyBankQueryModel convertLoyaltyBankEntityToLoyaltyBankQueryModel(LoyaltyBankEntity loyaltyBankEntity) {
        int available =
                loyaltyBankEntity.getEarned() - loyaltyBankEntity.getAuthorized() - loyaltyBankEntity.getCaptured();
        return new LoyaltyBankQueryModel(
                loyaltyBankEntity.getLoyaltyBankId(),
                loyaltyBankEntity.getAccountId(),
                loyaltyBankEntity.getPending(),
                loyaltyBankEntity.getEarned(),
                loyaltyBankEntity.getAuthorized(),
                loyaltyBankEntity.getCaptured(),
                available
        );
    }
}
