package loyalty.service.command.interceptors;

import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.exceptions.AccountExistsWithLoyaltyBankException;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

import static loyalty.service.core.constants.LogMessages.INTERCEPTED_COMMAND;

@Component
public class CreateLoyaltyBankCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateLoyaltyBankCommandInterceptor.class);

    private final LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    public CreateLoyaltyBankCommandInterceptor(LoyaltyBankLookupRepository loyaltyBankLookupRepository) {
        this.loyaltyBankLookupRepository = loyaltyBankLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {

            if (CreateLoyaltyBankCommand.class.equals(command.getPayloadType())) {
                LOGGER.info(String.format(INTERCEPTED_COMMAND, command.getPayloadType()));

                CreateLoyaltyBankCommand createLoyaltyBankCommand = (CreateLoyaltyBankCommand) command.getPayload();

                createLoyaltyBankCommand.validate();

                String accountId = createLoyaltyBankCommand.getAccountId();
                String businessName = createLoyaltyBankCommand.getBusinessName();

                List<LoyaltyBankLookupEntity> loyaltyBankLookupEntities = loyaltyBankLookupRepository.findByAccountId(accountId);

                // TODO: throw exception if account id does not exist

                boolean businessAlreadyAssignedToAccount = loyaltyBankLookupEntities.stream()
                        .anyMatch(entity -> entity.getBusinessName().equals(businessName));

                if (businessAlreadyAssignedToAccount) {
                    throw new AccountExistsWithLoyaltyBankException(accountId, businessName);
                }
            }

            return command;
        };
    }
}
