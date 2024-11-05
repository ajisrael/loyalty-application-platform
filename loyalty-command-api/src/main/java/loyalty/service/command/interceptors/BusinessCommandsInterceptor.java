package loyalty.service.command.interceptors;

import lombok.RequiredArgsConstructor;
import loyalty.service.command.commands.DeleteBusinessCommand;
import loyalty.service.command.commands.UpdateBusinessCommand;
import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.repositories.BusinessLookupRepository;
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
public class BusinessCommandsInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessCommandsInterceptor.class);

    private final BusinessLookupRepository businessLookupRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, genericCommand) -> {
            String commandName = genericCommand.getPayloadType().getSimpleName();
            LOGGER.debug(MarkerGenerator.generateMarker(genericCommand.getPayload()), INTERCEPTED_COMMAND, commandName);

            if (UpdateBusinessCommand.class.equals(genericCommand.getPayloadType())) {
                handleUpdateBusinessCommand((UpdateBusinessCommand) genericCommand.getPayload(), commandName);
            } else if (DeleteBusinessCommand.class.equals(genericCommand.getPayloadType())) {
                handleDeleteBusinessCommand((DeleteBusinessCommand) genericCommand.getPayload(), commandName);
            }

            return genericCommand;
        };
    }

    private void handleUpdateBusinessCommand(UpdateBusinessCommand command, String commandName) {
        throwExceptionIfBusinessDoesNotExist(command.getBusinessId(), command.getRequestId(), commandName);
    }

    private void handleDeleteBusinessCommand(DeleteBusinessCommand command, String commandName) {
        throwExceptionIfBusinessDoesNotExist(command.getBusinessId(), command.getRequestId(), commandName);
    }

    private void throwExceptionIfBusinessDoesNotExist(String businessId, String requestId, String commandName) {
        BusinessLookupEntity businessLookupEntity = businessLookupRepository.findByBusinessId(businessId);

        if (businessLookupEntity == null) {
            logAndThrowBusinessNotFoundException(businessId, requestId, commandName);
        }
    }

    private void logAndThrowBusinessNotFoundException(String businessId, String requestId, String commandName) {
        LOGGER.info(
                Markers.append(REQUEST_ID, requestId),
                BUSINESS_NOT_FOUND_CANCELLING_COMMAND, businessId, commandName
        );

        throw new BusinessNotFoundException(businessId);
    }
}