package loyalty.service.query.queryhandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.entities.LoyaltyBankEntity;
import loyalty.service.core.data.repositories.LoyaltyBankRepository;
import loyalty.service.core.exceptions.LoyaltyBankNotFoundException;
import loyalty.service.core.exceptions.NoLoyaltyBanksForAccountFoundException;
import loyalty.service.query.queries.FindAllLoyaltyBanksQuery;
import loyalty.service.query.queries.FindLoyaltyBankQuery;
import loyalty.service.query.queries.FindLoyaltyBanksWithAccountIdQuery;
import loyalty.service.query.querymodels.LoyaltyBankQueryModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class LoyaltyBankQueryHandler {

    private final LoyaltyBankRepository loyaltyBankRepository;

    @QueryHandler
    public Page<LoyaltyBankQueryModel> findAllLoyaltyBanks(FindAllLoyaltyBanksQuery query) {
        return loyaltyBankRepository.findAll(query.getPageable())
                .map(this::convertLoyaltyBankEntityToLoyaltyBankQueryModel);
    }

    @QueryHandler
    public List<LoyaltyBankQueryModel> findLoyaltyBanksWithAccountId(FindLoyaltyBanksWithAccountIdQuery query) {
        List<LoyaltyBankEntity> loyaltyBankEntities = loyaltyBankRepository.findByAccountId(query.getAccountId()).orElseThrow(
                () -> new NoLoyaltyBanksForAccountFoundException(query.getAccountId()));

        return loyaltyBankEntities.stream()
                .map(this::convertLoyaltyBankEntityToLoyaltyBankQueryModel)
                .toList();
    }

    @QueryHandler
    public LoyaltyBankQueryModel findLoyaltyBank(FindLoyaltyBankQuery query) {
        LoyaltyBankEntity loyaltyBankEntity = loyaltyBankRepository.findById(query.getLoyaltyBankId()).orElseThrow(
                () -> new LoyaltyBankNotFoundException(query.getLoyaltyBankId()));
        return convertLoyaltyBankEntityToLoyaltyBankQueryModel(loyaltyBankEntity);
    }

    private LoyaltyBankQueryModel convertLoyaltyBankEntityToLoyaltyBankQueryModel(LoyaltyBankEntity loyaltyBankEntity) {
        int available =
                loyaltyBankEntity.getEarned() - loyaltyBankEntity.getAuthorized() - loyaltyBankEntity.getCaptured();
        return new LoyaltyBankQueryModel(
                loyaltyBankEntity.getLoyaltyBankId(),
                loyaltyBankEntity.getAccountId(),
                loyaltyBankEntity.getBusinessName(),
                loyaltyBankEntity.getPending(),
                loyaltyBankEntity.getEarned(),
                loyaltyBankEntity.getAuthorized(),
                loyaltyBankEntity.getCaptured(),
                available
        );
    }
}
