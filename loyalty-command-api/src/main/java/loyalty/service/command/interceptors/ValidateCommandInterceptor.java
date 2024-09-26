package loyalty.service.command.interceptors;

import jakarta.annotation.Nonnull;
import loyalty.service.command.commands.AbstractCommand;
import net.logstash.logback.marker.Markers;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.VALIDATING_COMMAND;

@Component
public class ValidateCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

        private static final Logger LOGGER = LoggerFactory.getLogger(ValidateCommandInterceptor.class);

        @Nonnull
        @Override
        public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
                @Nonnull List<? extends CommandMessage<?>> messages) {
            return (index, genericCommand) -> {

                if (AbstractCommand.class.isAssignableFrom(genericCommand.getPayloadType())) {
                    AbstractCommand command = (AbstractCommand) genericCommand.getPayload();

                    LOGGER.info(
                            Markers.append(REQUEST_ID, command.getRequestId()),
                            VALIDATING_COMMAND,
                            genericCommand.getPayloadType().getSimpleName()
                    );

                    command.validate();
                }

                return genericCommand;
            };
        }
    }
