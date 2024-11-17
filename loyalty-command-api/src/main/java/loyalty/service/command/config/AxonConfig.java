package loyalty.service.command.config;

import loyalty.service.command.interceptors.*;
import loyalty.service.core.errorhandling.LoyaltyServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import static loyalty.service.core.constants.DomainConstants.*;

@Configuration
public class AxonConfig {

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
    }
}
