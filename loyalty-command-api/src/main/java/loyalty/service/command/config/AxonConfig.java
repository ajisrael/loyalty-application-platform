package loyalty.service.command.config;

import loyalty.service.command.interceptors.*;
import loyalty.service.core.errorhandling.LoyaltyServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

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
        // TODO: Save group strings to constants
        configurer.registerListenerInvocationErrorHandler("account-lookup-group",
                configuration -> new LoyaltyServiceEventsErrorHandler());
        configurer.registerListenerInvocationErrorHandler("business-lookup-group",
                configuration -> new LoyaltyServiceEventsErrorHandler());
        configurer.registerListenerInvocationErrorHandler("loyalty-bank-lookup-group",
                configuration -> new LoyaltyServiceEventsErrorHandler());
        configurer.registerListenerInvocationErrorHandler("redemption-tracker-group",
                configuration -> new LoyaltyServiceEventsErrorHandler());
        configurer.registerListenerInvocationErrorHandler("expiration-tracker-group",
                configuration -> new LoyaltyServiceEventsErrorHandler());

    }
}
