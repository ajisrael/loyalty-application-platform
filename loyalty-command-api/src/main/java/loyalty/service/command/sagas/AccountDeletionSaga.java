package loyalty.service.command.sagas;

import loyalty.service.command.commands.DeleteLoyaltyBankCommand;
import loyalty.service.command.commands.ExpireAllPointsCommand;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.events.AccountDeletedEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.AllPointsExpiredEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

@Saga
public class AccountDeletionSaga implements Serializable {

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    @SagaEventHandler(associationProperty = "accountId")
    @StartSaga
    public void handle(AccountDeletedEvent event) {
        String accountId = event.getAccountId();
        SagaLifecycle.associateWith("accountId", accountId);

        List<LoyaltyBankLookupEntity> loyaltyBankLookupEntities = loyaltyBankLookupRepository.findByAccountId(accountId);

        if (loyaltyBankLookupEntities.isEmpty()) {
            SagaLifecycle.end();
            return;
        }

        loyaltyBankLookupEntities.forEach(
                loyaltyBankLookupEntity -> commandGateway.send(
                        ExpireAllPointsCommand.builder()
                                .loyaltyBankId(loyaltyBankLookupEntity.getLoyaltyBankId())
                                .build()
                )
        );
    }

    @SagaEventHandler(associationProperty = "accountId")
    public void handle(AllPointsExpiredEvent event) {
        commandGateway.send(DeleteLoyaltyBankCommand.builder()
                .loyaltyBankId(event.getLoyaltyBankId())
                .build()
        );
    }

    @SagaEventHandler(associationProperty = "accountId")
    public void handle(LoyaltyBankDeletedEvent event) {
        List<LoyaltyBankLookupEntity> loyaltyBankLookupEntities = loyaltyBankLookupRepository.findByAccountId(event.getAccountId());

        if (loyaltyBankLookupEntities.isEmpty()) {
            SagaLifecycle.end();
        }
    }
}
