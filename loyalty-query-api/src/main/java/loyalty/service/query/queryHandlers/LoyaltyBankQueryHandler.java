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
import java.util.Optional;

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
        Optional<List<LoyaltyBankEntity>> loyaltyBankEntitiesOptional = loyaltyBankRepository.findByAccountId(query.getAccountId());

        if (loyaltyBankEntitiesOptional.isEmpty() || loyaltyBankEntitiesOptional.get().isEmpty()) {
            throw new NoLoyaltyBanksForAccountFoundException(query.getAccountId());
        }

        return loyaltyBankEntitiesOptional.get().stream()
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
