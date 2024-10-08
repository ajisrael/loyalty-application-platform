package loyalty.service.query.queryHandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.exceptions.NoLoyaltyBanksForBusinessFoundException;
import loyalty.service.query.data.entities.LoyaltyBankEntity;
import loyalty.service.query.data.repositories.LoyaltyBankRepository;
import loyalty.service.core.exceptions.LoyaltyBankNotFoundException;
import loyalty.service.core.exceptions.NoLoyaltyBanksForAccountFoundException;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.query.queries.FindAllLoyaltyBanksQuery;
import loyalty.service.query.queries.FindLoyaltyBankQuery;
import loyalty.service.query.queries.FindLoyaltyBanksWithAccountIdQuery;
import loyalty.service.query.queries.FindLoyaltyBanksWithBusinessIdQuery;
import loyalty.service.query.queryModels.LoyaltyBankQueryModel;
import net.logstash.logback.marker.Markers;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;

@Component
@AllArgsConstructor
public class LoyaltyBankQueryHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankQueryHandler.class);
    private final LoyaltyBankRepository loyaltyBankRepository;

    @QueryHandler
    public Page<LoyaltyBankQueryModel> findAllLoyaltyBanks(FindAllLoyaltyBanksQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        return loyaltyBankRepository.findAll(query.getPageable())
                .map(this::convertLoyaltyBankEntityToLoyaltyBankQueryModel);
    }

    @QueryHandler
    public List<LoyaltyBankQueryModel> findLoyaltyBanksWithAccountId(FindLoyaltyBanksWithAccountIdQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        String accountId = query.getAccountId();

        Optional<List<LoyaltyBankEntity>> loyaltyBankEntitiesOptional = loyaltyBankRepository.findByAccountId(accountId);

        if (loyaltyBankEntitiesOptional.isEmpty() || loyaltyBankEntitiesOptional.get().isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), NO_LOYALTY_BANK_FOUND_FOR_ACCOUNT, accountId);
            throw new NoLoyaltyBanksForAccountFoundException(accountId);
        }

        return loyaltyBankEntitiesOptional.get().stream()
                .map(this::convertLoyaltyBankEntityToLoyaltyBankQueryModel)
                .toList();
    }

    @QueryHandler
    public List<LoyaltyBankQueryModel> findLoyaltyBanksWithBusinessId(FindLoyaltyBanksWithBusinessIdQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        String accountId = query.getBusinessId();

        Optional<List<LoyaltyBankEntity>> loyaltyBankEntitiesOptional = loyaltyBankRepository.findByBusinessId(accountId);

        if (loyaltyBankEntitiesOptional.isEmpty() || loyaltyBankEntitiesOptional.get().isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), NO_LOYALTY_BANK_FOUND_FOR_BUSINESS, accountId);
            throw new NoLoyaltyBanksForBusinessFoundException(accountId);
        }

        return loyaltyBankEntitiesOptional.get().stream()
                .map(this::convertLoyaltyBankEntityToLoyaltyBankQueryModel)
                .toList();
    }

    @QueryHandler
    public LoyaltyBankQueryModel findLoyaltyBank(FindLoyaltyBankQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        String loyaltyBankId = query.getLoyaltyBankId();

        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findById(query.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), LOYALTY_BANK_NOT_FOUND_IN_DB, loyaltyBankId);
            throw new LoyaltyBankNotFoundException(loyaltyBankId);
        }

        return convertLoyaltyBankEntityToLoyaltyBankQueryModel(loyaltyBankEntityOptional.get());
    }

    private LoyaltyBankQueryModel convertLoyaltyBankEntityToLoyaltyBankQueryModel(LoyaltyBankEntity loyaltyBankEntity) {
        int available =
                loyaltyBankEntity.getEarned() - loyaltyBankEntity.getAuthorized() - loyaltyBankEntity.getCaptured();
        return new LoyaltyBankQueryModel(
                loyaltyBankEntity.getLoyaltyBankId(),
                loyaltyBankEntity.getAccountId(),
                loyaltyBankEntity.getBusinessId(),
                loyaltyBankEntity.getPending(),
                loyaltyBankEntity.getEarned(),
                loyaltyBankEntity.getAuthorized(),
                loyaltyBankEntity.getCaptured(),
                available
        );
    }
}
