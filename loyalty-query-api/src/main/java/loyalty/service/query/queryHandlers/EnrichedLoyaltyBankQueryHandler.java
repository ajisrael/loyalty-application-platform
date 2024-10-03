package loyalty.service.query.queryHandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.exceptions.*;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.query.data.entities.AccountEntity;
import loyalty.service.query.data.entities.BusinessEntity;
import loyalty.service.query.data.entities.LoyaltyBankEntity;
import loyalty.service.query.data.repositories.AccountRepository;
import loyalty.service.query.data.repositories.BusinessRepository;
import loyalty.service.query.data.repositories.LoyaltyBankRepository;
import loyalty.service.query.queries.*;
import loyalty.service.query.queryModels.*;
import net.logstash.logback.marker.Markers;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;

@Component
@AllArgsConstructor
public class EnrichedLoyaltyBankQueryHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(EnrichedLoyaltyBankQueryHandler.class);
    private final AccountRepository accountRepository;
    private final LoyaltyBankRepository loyaltyBankRepository;
    private final BusinessRepository businessRepository;

    @QueryHandler
    public EnrichedLoyaltyBanksForAccountQueryModel findAndEnrichLoyaltyBanksForAccount(FindEnrichedLoyaltyBanksForAccountQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        String accountId = query.getAccountId();

        Optional<AccountEntity> accountEntityOptional = accountRepository.findByAccountId(accountId);

        if (accountEntityOptional.isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), ACCOUNT_NOT_FOUND_IN_DB, accountId);
            throw new AccountNotFoundException(accountId);
        }

        AccountEntity accountEntity = accountEntityOptional.get();

        EnrichedLoyaltyBanksForAccountQueryModel enrichedLoyaltyBanksForAccountQueryModel =
                new EnrichedLoyaltyBanksForAccountQueryModel(
                        accountEntity.getAccountId(),
                        accountEntity.getFirstName(),
                        accountEntity.getLastName(),
                        accountEntity.getEmail(),
                        new ArrayList<>()
                );

        Optional<List<LoyaltyBankEntity>> loyaltyBankEntitiesOptional = loyaltyBankRepository.findByAccountId(accountId);

        if (loyaltyBankEntitiesOptional.isEmpty() || loyaltyBankEntitiesOptional.get().isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), NO_LOYALTY_BANK_FOUND_FOR_ACCOUNT, accountId);
        }

        loyaltyBankEntitiesOptional.get().forEach(
                loyaltyBankEntity -> {
                    String businessId = loyaltyBankEntity.getBusinessId();
                    Optional<BusinessEntity> businessEntityOptional = businessRepository.findByBusinessId(businessId);

                    if (businessEntityOptional.isEmpty()) {
                        LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), BUSINESS_NOT_FOUND_IN_DB, businessId);
                        throw new BusinessNotFoundException(businessId);
                    }

                    BusinessEntity businessEntity = businessEntityOptional.get();
                    BusinessQueryModel businessQueryModel = new BusinessQueryModel(businessEntity.getBusinessId(), businessEntity.getBusinessName());
                    enrichedLoyaltyBanksForAccountQueryModel.addLoyaltyBank(new EnrichedLoyaltyBankForAccountQueryModel(
                            loyaltyBankEntity.getLoyaltyBankId(),
                            businessQueryModel,
                            loyaltyBankEntity.getPending(),
                            loyaltyBankEntity.getEarned(),
                            loyaltyBankEntity.getAuthorized(),
                            loyaltyBankEntity.getCaptured(),
                            loyaltyBankEntity.getEarned() - loyaltyBankEntity.getAuthorized() - loyaltyBankEntity.getCaptured()
                    ));
                }
        );

        return enrichedLoyaltyBanksForAccountQueryModel;
    }

    @QueryHandler
    public EnrichedLoyaltyBanksForBusinessQueryModel findAndEnrichLoyaltyBanksForBusiness(FindEnrichedLoyaltyBanksForBusinessQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_QUERY, query.getClass().getSimpleName());

        String businessId = query.getBusinessId();

        Optional<BusinessEntity> businessEntityOptional = businessRepository.findByBusinessId(businessId);

        if (businessEntityOptional.isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), ACCOUNT_NOT_FOUND_IN_DB, businessId);
            throw new AccountNotFoundException(businessId);
        }

        BusinessEntity businessEntity = businessEntityOptional.get();

        EnrichedLoyaltyBanksForBusinessQueryModel enrichedLoyaltyBanksForBusinessQueryModel =
                new EnrichedLoyaltyBanksForBusinessQueryModel(
                        businessEntity.getBusinessId(),
                        businessEntity.getBusinessId(),
                        new ArrayList<>()
                );

        Optional<List<LoyaltyBankEntity>> loyaltyBankEntitiesOptional = loyaltyBankRepository.findByBusinessId(businessId);

        if (loyaltyBankEntitiesOptional.isEmpty() || loyaltyBankEntitiesOptional.get().isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), NO_LOYALTY_BANK_FOUND_FOR_BUSINESS, businessId);
        }

        loyaltyBankEntitiesOptional.get().forEach(
                loyaltyBankEntity -> {
                    String accountId = loyaltyBankEntity.getAccountId();
                    Optional<AccountEntity> accountEntityOptional = accountRepository.findByAccountId(accountId);

                    if (accountEntityOptional.isEmpty()) {
                        LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), BUSINESS_NOT_FOUND_IN_DB, accountId);
                        throw new AccountNotFoundException(accountId);
                    }

                    AccountEntity accountEntity = accountEntityOptional.get();
                    AccountQueryModel accountQueryModel = new AccountQueryModel(
                            accountEntity.getAccountId(),
                            accountEntity.getFirstName(),
                            accountEntity.getLastName(),
                            accountEntity.getEmail()
                    );

                    enrichedLoyaltyBanksForBusinessQueryModel.addLoyaltyBank(new EnrichedLoyaltyBankForBusinessQueryModel(
                            loyaltyBankEntity.getLoyaltyBankId(),
                            accountQueryModel,
                            loyaltyBankEntity.getPending(),
                            loyaltyBankEntity.getEarned(),
                            loyaltyBankEntity.getAuthorized(),
                            loyaltyBankEntity.getCaptured(),
                            loyaltyBankEntity.getEarned() - loyaltyBankEntity.getAuthorized() - loyaltyBankEntity.getCaptured()
                    ));
                }
        );

        return enrichedLoyaltyBanksForBusinessQueryModel;
    }

}
