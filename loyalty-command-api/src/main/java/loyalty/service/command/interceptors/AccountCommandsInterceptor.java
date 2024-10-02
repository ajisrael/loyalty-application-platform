package loyalty.service.command.interceptors;

import lombok.RequiredArgsConstructor;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.core.exceptions.AccountNotFoundException;
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
public class AccountCommandsInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCommandsInterceptor.class);

    private final AccountLookupRepository accountLookupRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, genericCommand) -> {

            if (CreateAccountCommand.class.equals(genericCommand.getPayloadType())) {
                CreateAccountCommand command = (CreateAccountCommand) genericCommand.getPayload();

                String commandName = command.getClass().getSimpleName();
                LOGGER.info(MarkerGenerator.generateMarker(command), INTERCEPTED_COMMAND, commandName);

                String email = command.getEmail();
                AccountLookupEntity accountLookupEntity = accountLookupRepository.findByEmail(email);

                if (accountLookupEntity != null) {
                    logAndThrowEmailAlreadyExistsException(accountLookupEntity, command.getRequestId(), commandName);
                }
            }

            if (UpdateAccountCommand.class.equals(genericCommand.getPayloadType())) {
                UpdateAccountCommand command = (UpdateAccountCommand) genericCommand.getPayload();

                String commandName = command.getClass().getSimpleName();
                LOGGER.info(MarkerGenerator.generateMarker(command), INTERCEPTED_COMMAND, commandName);

                String accountId = command.getAccountId();
                throwExceptionIfAccountDoesNotExist(accountId, command.getRequestId(), commandName);

                String email = command.getEmail();
                AccountLookupEntity accountLookupEntity = accountLookupRepository.findByEmail(email);

                if (accountLookupEntity != null && !accountLookupEntity.getAccountId().equals(accountId)) {
                    logAndThrowEmailAlreadyExistsException(accountLookupEntity, command.getRequestId(), commandName);
                }
            }

            if (DeleteAccountCommand.class.equals(genericCommand.getPayloadType())) {
                DeleteAccountCommand command = (DeleteAccountCommand) genericCommand.getPayload();

                String commandName = command.getClass().getSimpleName();
                LOGGER.info(MarkerGenerator.generateMarker(command), INTERCEPTED_COMMAND, commandName);

                String accountId = command.getAccountId();

                throwExceptionIfAccountDoesNotExist(accountId, command.getRequestId(), commandName);
            }

            return genericCommand;
        };
    }

    private void logAndThrowEmailAlreadyExistsException(AccountLookupEntity accountLookupEntity, String requestId, String commandName) {
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
}
