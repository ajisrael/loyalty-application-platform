package loyalty.service.command.interceptors;

import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.core.exceptions.EmailExistsForAccountException;
import loyalty.service.core.utils.MarkerGenerator;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

import static loyalty.service.core.constants.LogMessages.INTERCEPTED_COMMAND;
import static loyalty.service.core.constants.ExceptionMessages.ACCOUNT_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;

@Component
public class UpdateAccountCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAccountCommandInterceptor.class);

    private final AccountLookupRepository accountLookupRepository;

    public UpdateAccountCommandInterceptor(AccountLookupRepository accountLookupRepository) {
        this.accountLookupRepository = accountLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {

            if (UpdateAccountCommand.class.equals(command.getPayloadType())) {
                LOGGER.info(
                        MarkerGenerator.generateMarker(command),
                        String.format(INTERCEPTED_COMMAND, command.getClass().getSimpleName())
                );

                UpdateAccountCommand updateAccountCommand = (UpdateAccountCommand) command.getPayload();

                updateAccountCommand.validate();

                String accountId = updateAccountCommand.getAccountId();
                AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(accountId);

                throwExceptionIfEntityDoesNotExist(accountLookupEntity,
                        String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, accountId));

                String email = updateAccountCommand.getEmail();
                AccountLookupEntity emailAccountLookupEntity = accountLookupRepository.findByEmail(email);

                if (emailAccountLookupEntity != null && !emailAccountLookupEntity.getAccountId().equals(accountId)) {
                    throw new EmailExistsForAccountException(email);
                }
            }

            return command;
        };
    }
}