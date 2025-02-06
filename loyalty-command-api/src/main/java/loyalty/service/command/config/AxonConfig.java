package loyalty.service.command.config;

import loyalty.service.command.interceptors.*;
import loyalty.service.command.projections.BusinessLookupEventsHandler;
import loyalty.service.command.sagas.BusinessDeletionSaga;
import loyalty.service.core.errorhandling.LoyaltyServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static loyalty.service.core.constants.DomainConstants.*;

@Configuration
public class AxonConfig {

    private static final Logger logger = LoggerFactory.getLogger(AxonConfig.class);

    @Bean
    public SnapshotTriggerDefinition accountSnapshotTrigger(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 10);
    }

    @Autowired
    public void registerAccountCommandInterceptors(ApplicationContext context, CommandBus commandBus) {
        commandBus.registerDispatchInterceptor(
                context.getBean(ValidateCommandInterceptor.class)
        );
        commandBus.registerDispatchInterceptor(
                context.getBean(AccountCommandsInterceptor.class)
        );
        commandBus.registerDispatchInterceptor(
                context.getBean(BusinessCommandsInterceptor.class)
        );
        commandBus.registerDispatchInterceptor(
                context.getBean(LoyaltyBankCommandsInterceptor.class)
        );
        commandBus.registerDispatchInterceptor(
                context.getBean(TransactionCommandsInterceptor.class)
        );
    }

    @Autowired
    public void configure(EventProcessingConfigurer configurer) {
        configurer.registerListenerInvocationErrorHandler(COMMAND_PROJECTION_GROUP,
                configuration -> new LoyaltyServiceEventsErrorHandler());
        configurer.registerListenerInvocationErrorHandler(REDEMPTION_TRACKER_GROUP,
                configuration -> new LoyaltyServiceEventsErrorHandler());
        configurer.registerListenerInvocationErrorHandler(EXPIRATION_TRACKER_GROUP,
                configuration -> new LoyaltyServiceEventsErrorHandler());

        configurer.assignHandlerInstancesMatching(BUSINESS_PROJECTION_GROUP, handler -> {
            boolean matches = handler instanceof BusinessDeletionSaga;
            if (matches) {
                logger.info("BusinessDeletionSaga assigned to business-projection-group");
            }
            return matches;
        });

        configurer.assignHandlerInstancesMatching(BUSINESS_PROJECTION_GROUP, handler -> {
            boolean matches = handler instanceof BusinessLookupEventsHandler;
            if (matches) {
                logger.info("BusinessLookupEventsHandler assigned to business-projection-group");
            }
            return matches;
        });
        configurer.assignProcessingGroup(BUSINESS_PROJECTION_GROUP, "BusinessDeletionSagaProcessor");
    }
}
