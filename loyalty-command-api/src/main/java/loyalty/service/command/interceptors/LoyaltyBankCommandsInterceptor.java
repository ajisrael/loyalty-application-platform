package loyalty.service.command.interceptors;

import lombok.RequiredArgsConstructor;
import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.command.data.repositories.BusinessLookupRepository;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.exceptions.AccountExistsWithLoyaltyBankException;
import loyalty.service.core.exceptions.AccountNotFoundException;
import loyalty.service.core.exceptions.BusinessNotFoundException;
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
@RequiredArgsConstructor
public class LoyaltyBankCommandsInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankCommandsInterceptor.class);

    private final AccountLookupRepository accountLookupRepository;
    private final BusinessLookupRepository businessLookupRepository;
    private final LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, genericCommand) -> {
            String commandName = genericCommand.getPayloadType().getSimpleName();
            LOGGER.debug(MarkerGenerator.generateMarker(genericCommand.getPayload()), INTERCEPTED_COMMAND, commandName);

            if (CreateLoyaltyBankCommand.class.equals(genericCommand.getPayloadType())) {
                handleCreateLoyaltyBankCommand((CreateLoyaltyBankCommand) genericCommand.getPayload(), commandName);
            }

            return genericCommand;
        };
    }

    private void handleCreateLoyaltyBankCommand(CreateLoyaltyBankCommand command, String commandName) {
        String accountId = command.getAccountId();
        throwExceptionIfAccountDoesNotExist(accountId, command.getRequestId(), commandName);

        String businessId = command.getBusinessId();
        throwExceptionIfBusinessDoesNotExist(businessId, command.getRequestId(), commandName);

        throwExceptionIfAccountAlreadyEnrolledInBusiness(accountId, businessId, command.getRequestId(), commandName);
    }

    private void throwExceptionIfAccountDoesNotExist(String accountId, String requestId, String commandName) {
        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(accountId);

        if (accountLookupEntity == null) {
            LOGGER.info(
                    Markers.append(REQUEST_ID, requestId),
                    ACCOUNT_NOT_FOUND_CANCELLING_COMMAND, accountId, commandName
            );

            throw new AccountNotFoundException(accountId);
        }
    }

    private void throwExceptionIfBusinessDoesNotExist(String businessId, String requestId, String commandName) {
        BusinessLookupEntity businessLookupEntity = businessLookupRepository.findByBusinessId(businessId);

        if (businessLookupEntity == null) {
            LOGGER.info(
                    Markers.append(REQUEST_ID, requestId),
                    BUSINESS_NOT_FOUND_CANCELLING_COMMAND, businessId, commandName
            );

            throw new BusinessNotFoundException(businessId);
        }
    }

    private void throwExceptionIfAccountAlreadyEnrolledInBusiness(String accountId, String businessId, String requestId, String commandName) {
        LoyaltyBankLookupEntity loyaltyBankLookupEntity = loyaltyBankLookupRepository.findByBusinessIdAndAccountId(businessId, accountId);

        if (loyaltyBankLookupEntity != null) {
            LOGGER.info(
                    Markers.append(REQUEST_ID, requestId),
                    ACCOUNT_ALREADY_ENROLLED_IN_BUSINESS_CANCELLING_COMMAND,
                    accountId, businessId, commandName
            );

            throw new AccountExistsWithLoyaltyBankException(accountId, businessId);
        }
    }
}