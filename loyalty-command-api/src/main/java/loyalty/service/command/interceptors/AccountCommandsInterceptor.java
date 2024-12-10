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
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
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
            String commandName = genericCommand.getPayloadType().getSimpleName();
            LOGGER.debug(MarkerGenerator.generateMarker(genericCommand.getPayload()), INTERCEPTED_COMMAND, commandName);

            if (CreateAccountCommand.class.equals(genericCommand.getPayloadType())) {
                handleCreateAccountCommand((CreateAccountCommand) genericCommand.getPayload(), commandName);
            } else if (UpdateAccountCommand.class.equals(genericCommand.getPayloadType())) {
                handleUpdateAccountCommand((UpdateAccountCommand) genericCommand.getPayload(), commandName);
                UpdateAccountCommand updateAccountCommand = (UpdateAccountCommand) genericCommand.getPayload();
                CreateAccountCommand createAccountCommand = CreateAccountCommand.builder()
                        .requestId(updateAccountCommand.getRequestId())
                        .accountId(UUID.randomUUID().toString())
                        .firstName("INTERCEPTED_NAME")
                        .lastName(updateAccountCommand.getLastName())
                        .email(updateAccountCommand.getEmail())
                        .build();
                return GenericCommandMessage.asCommandMessage(createAccountCommand);
            } else if (DeleteAccountCommand.class.equals(genericCommand.getPayloadType())) {
                handleDeleteAccountCommand((DeleteAccountCommand) genericCommand.getPayload(), commandName);
            }

            return genericCommand;
        };
    }

    private void handleCreateAccountCommand(CreateAccountCommand command, String commandName) {
        throwExceptionIfEmailExists(command.getEmail(), command.getRequestId(), commandName);
    }

    private void handleUpdateAccountCommand(UpdateAccountCommand command, String commandName) {
        String accountId = command.getAccountId();
        String requestId = command.getRequestId();
        throwExceptionIfAccountDoesNotExist(accountId, requestId, commandName);
        throwExceptionIfEmailExistsForAnotherAccount(command.getEmail(), accountId, requestId, commandName);
    }

    private void handleDeleteAccountCommand(DeleteAccountCommand command, String commandName) {
        throwExceptionIfAccountDoesNotExist(command.getAccountId(), command.getRequestId(), commandName);
    }

    private void throwExceptionIfEmailExists(String email, String requestId, String commandName) {
        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByEmail(email);

        if (accountLookupEntity != null) {
            logAndThrowEmailExistsForAccountException(accountLookupEntity, requestId, commandName);
        }
    }

    private void throwExceptionIfEmailExistsForAnotherAccount(String email, String accountId, String requestId, String commandName) {
        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByEmail(email);
        if (accountLookupEntity != null && !accountLookupEntity.getAccountId().equals(accountId)) {
            logAndThrowEmailExistsForAccountException(accountLookupEntity, requestId, commandName);
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