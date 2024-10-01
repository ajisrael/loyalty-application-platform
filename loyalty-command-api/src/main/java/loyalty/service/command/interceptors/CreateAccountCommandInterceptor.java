package loyalty.service.command.interceptors;

import lombok.RequiredArgsConstructor;
import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
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
import static loyalty.service.core.constants.LogMessages.EMAIL_FOUND_ON_ANOTHER_ACCOUNT_CANCELLING_COMMAND;
import static loyalty.service.core.constants.LogMessages.INTERCEPTED_COMMAND;

@Component
@RequiredArgsConstructor
public class CreateAccountCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAccountCommandInterceptor.class);

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
                    Marker marker = MarkerGenerator.generateMarker(accountLookupEntity);
                    marker.add(Markers.append(REQUEST_ID, command.getRequestId()));
                    LOGGER.info(
                            marker,
                            EMAIL_FOUND_ON_ANOTHER_ACCOUNT_CANCELLING_COMMAND,
                            accountLookupEntity.getAccountId(),
                            commandName
                    );

                    throw new EmailExistsForAccountException(email);
                }
            }

            return genericCommand;
        };
    }
}
