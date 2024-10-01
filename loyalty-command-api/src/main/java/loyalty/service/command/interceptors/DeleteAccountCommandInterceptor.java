package loyalty.service.command.interceptors;

import lombok.RequiredArgsConstructor;
import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
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
import static loyalty.service.core.constants.LogMessages.ACCOUNT_NOT_FOUND_CANCELLING_COMMAND;
import static loyalty.service.core.constants.LogMessages.INTERCEPTED_COMMAND;

@Component
@RequiredArgsConstructor
public class DeleteAccountCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAccountCommandInterceptor.class);

    private final AccountLookupRepository accountLookupRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, genericCommand) -> {

            if (DeleteAccountCommand.class.equals(genericCommand.getPayloadType())) {
                DeleteAccountCommand command = (DeleteAccountCommand) genericCommand.getPayload();

                String commandName = command.getClass().getSimpleName();
                LOGGER.info(MarkerGenerator.generateMarker(command), INTERCEPTED_COMMAND, commandName);

                String accountId = command.getAccountId();
                AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(accountId);

                if (accountLookupEntity == null) {
                    LOGGER.info(
                            Markers.append(REQUEST_ID, command.getRequestId()),
                            ACCOUNT_NOT_FOUND_CANCELLING_COMMAND, accountId, commandName
                    );

                    throw new AccountNotFoundException(accountId);
                }
            }

            return genericCommand;
        };
    }
}
