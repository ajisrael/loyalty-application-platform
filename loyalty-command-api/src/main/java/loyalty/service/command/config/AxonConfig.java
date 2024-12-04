package loyalty.service.command.config;

import loyalty.service.command.interceptors.*;
import loyalty.service.core.errorhandling.LoyaltyServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.ConfigurerModule;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static loyalty.service.core.constants.DomainConstants.*;

@Configuration
public class AxonConfig {

    @Bean
    public ConfigurerModule commandInterceptorConfigurerModule(ApplicationContext context) {
        return configurer -> configurer.onInitialize(config -> {
            CommandBus commandBus = config.commandBus();
            commandBus.registerDispatchInterceptor(context.getBean(ValidateCommandInterceptor.class));
            commandBus.registerDispatchInterceptor(context.getBean(AccountCommandsInterceptor.class));
            commandBus.registerDispatchInterceptor(context.getBean(BusinessCommandsInterceptor.class));
            commandBus.registerDispatchInterceptor(context.getBean(LoyaltyBankCommandsInterceptor.class));
            commandBus.registerDispatchInterceptor(context.getBean(TransactionCommandsInterceptor.class));
        });
    }

    @Bean
    public ConfigurerModule eventProcessorErrorHandlerConfigurerModule() {
        return configurer -> configurer.eventProcessing()
                .registerListenerInvocationErrorHandler(
                        COMMAND_PROJECTION_GROUP,
                        config -> new LoyaltyServiceEventsErrorHandler()
                )
                .registerListenerInvocationErrorHandler(
                        REDEMPTION_TRACKER_GROUP,
                        config -> new LoyaltyServiceEventsErrorHandler()
                )
                .registerListenerInvocationErrorHandler(
                        EXPIRATION_TRACKER_GROUP,
                        config -> new LoyaltyServiceEventsErrorHandler()
                );
    }
}
