package loyalty.service;

import loyalty.service.command.interceptors.*;
import loyalty.service.core.errorhandling.LoyaltyServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
public class LoyaltyCommandApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoyaltyCommandApiApplication.class, args);
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
