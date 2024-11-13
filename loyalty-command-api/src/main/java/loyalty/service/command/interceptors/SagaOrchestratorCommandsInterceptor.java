package loyalty.service.command.interceptors;

import lombok.RequiredArgsConstructor;
import loyalty.service.command.commands.StartAccountAndLoyaltyBankCreationCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.command.data.repositories.BusinessLookupRepository;
import loyalty.service.core.exceptions.BusinessNotFoundException;
import loyalty.service.core.exceptions.EmailExistsForAccountException;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;

@Component
@RequiredArgsConstructor
public class SagaOrchestratorCommandsInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaOrchestratorCommandsInterceptor.class);

    private final AccountLookupRepository accountLookupRepository;
    private final BusinessLookupRepository businessLookupRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, genericCommand) -> {
            String commandName = genericCommand.getPayloadType().getSimpleName();
            LOGGER.debug(MarkerGenerator.generateMarker(genericCommand.getPayload()), INTERCEPTED_COMMAND, commandName);

            if (StartAccountAndLoyaltyBankCreationCommand.class.equals(genericCommand.getPayloadType())) {
                handleStartAccountAndLoyaltyBankCreationCommand((StartAccountAndLoyaltyBankCreationCommand) genericCommand.getPayload(), commandName);
            }

            return genericCommand;
        };
    }

    private void handleStartAccountAndLoyaltyBankCreationCommand(StartAccountAndLoyaltyBankCreationCommand command, String commandName) {
        // TODO add checks for existing accountId and loyaltyBankId as the aggregate identifier won't reject until the saga has started
        throwExceptionIfEmailExists(command.getEmail(), command.getRequestId(), commandName);
        throwExceptionIfBusinessDoesNotExist(command.getBusinessId(), command.getRequestId(), commandName);
    }

    // TODO: move shared lookup and exception handling to common service or util class
    private void throwExceptionIfEmailExists(String email, String requestId, String commandName) {
        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByEmail(email);

        if (accountLookupEntity != null) {
            logAndThrowEmailExistsForAccountException(accountLookupEntity, requestId, commandName);
        }
    }

    private void throwExceptionIfBusinessDoesNotExist(String businessId, String requestId, String commandName) {
        BusinessLookupEntity businessLookupEntity = businessLookupRepository.findByBusinessId(businessId);

        if (businessLookupEntity == null) {
            logAndThrowBusinessNotFoundException(businessId, requestId, commandName);
        }
    }

    private void logAndThrowEmailExistsForAccountException(AccountLookupEntity accountLookupEntity, String requestId, String commandName) {
        Marker marker = MarkerGenerator.generateMarker(accountLookupEntity);
        marker.add(Markers.append(REQUEST_ID, requestId));
        LOGGER.info(
                marker,
                EMAIL_FOUND_ON_ANOTHER_ACCOUNT_CANCELLING_COMMAND,
                accountLookupEntity.getAccountId(),
                commandName
        );

        throw new EmailExistsForAccountException(accountLookupEntity.getEmail());
    }

    private void logAndThrowBusinessNotFoundException(String businessId, String requestId, String commandName) {
        LOGGER.info(
                Markers.append(REQUEST_ID, requestId),
                BUSINESS_NOT_FOUND_CANCELLING_COMMAND, businessId, commandName
        );

        throw new BusinessNotFoundException(businessId);
    }
}