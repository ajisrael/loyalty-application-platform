package loyalty.service.command.interceptors;

import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.core.data.entities.AccountLookupEntity;
import loyalty.service.core.data.repositories.AccountLookupRepository;
import loyalty.service.core.exceptions.EmailExistsForAccountException;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

import static loyalty.service.core.constants.LogMessages.INTERCEPTED_COMMAND;

@Component
public class CreateAccountCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAccountCommandInterceptor.class);

    private final AccountLookupRepository accountLookupRepository;

    public CreateAccountCommandInterceptor(AccountLookupRepository accountLookupRepository) {
        this.accountLookupRepository = accountLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {

            if (CreateAccountCommand.class.equals(command.getPayloadType())) {
                LOGGER.info(String.format(INTERCEPTED_COMMAND, command.getPayloadType()));

                CreateAccountCommand createAccountCommand = (CreateAccountCommand) command.getPayload();

                createAccountCommand.validate();

                String email = createAccountCommand.getEmail();

                AccountLookupEntity accountLookupEntity = accountLookupRepository.findByEmail(email);

                if (accountLookupEntity != null) {
                    throw new EmailExistsForAccountException(email);
                }
            }

            return command;
        };
    }
}
