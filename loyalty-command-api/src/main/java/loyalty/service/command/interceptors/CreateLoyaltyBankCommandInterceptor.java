package loyalty.service.command.interceptors;

import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.exceptions.AccountExistsWithLoyaltyBankException;
import loyalty.service.core.exceptions.AccountNotFoundException;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;

@Component
public class CreateLoyaltyBankCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateLoyaltyBankCommandInterceptor.class);

    private final AccountLookupRepository accountLookupRepository;
    private final LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    public CreateLoyaltyBankCommandInterceptor(
            AccountLookupRepository accountLookupRepository, LoyaltyBankLookupRepository loyaltyBankLookupRepository) {
        this.accountLookupRepository = accountLookupRepository;
        this.loyaltyBankLookupRepository = loyaltyBankLookupRepository;
    }

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, genericCommand) -> {

            if (CreateLoyaltyBankCommand.class.equals(genericCommand.getPayloadType())) {
                CreateLoyaltyBankCommand command = (CreateLoyaltyBankCommand) genericCommand.getPayload();

                String commandName = command.getClass().getSimpleName();
                LOGGER.info(MarkerGenerator.generateMarker(command), INTERCEPTED_COMMAND, commandName);

                command.validate();

                String accountId = command.getAccountId();
                AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(accountId);

                if (accountLookupEntity == null) {
                    LOGGER.info(
                            Markers.append(REQUEST_ID, command.getRequestId()),
                            ACCOUNT_NOT_FOUND_CANCELLING_COMMAND, accountId, commandName
                    );

                    throw new AccountNotFoundException(accountId);
                }

                String businessName = command.getBusinessName();
                List<LoyaltyBankLookupEntity> loyaltyBankLookupEntities = loyaltyBankLookupRepository.findByAccountId(accountId);

                boolean businessAlreadyAssignedToAccount = loyaltyBankLookupEntities.stream()
                        .anyMatch(entity -> entity.getBusinessName().equals(businessName));

                if (businessAlreadyAssignedToAccount) {
                    LOGGER.info(
                            Markers.append(REQUEST_ID, command.getRequestId()),
                            ACCOUNT_ALREADY_ENROLLED_IN_BUSINESS_CANCELLING_COMMAND,
                            accountId, businessName, commandName
                    );

                    throw new AccountExistsWithLoyaltyBankException(accountId, businessName);
                }
            }

            return genericCommand;
        };
    }
}
