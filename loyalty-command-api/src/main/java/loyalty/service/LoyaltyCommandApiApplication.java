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

@EnableDiscoveryClient
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
				context.getBean(CreateAccountCommandInterceptor.class)
		);
		commandBus.registerDispatchInterceptor(
				context.getBean(UpdateAccountCommandInterceptor.class)
		);
		commandBus.registerDispatchInterceptor(
				context.getBean(DeleteAccountCommandInterceptor.class)
		);
		commandBus.registerDispatchInterceptor(
				context.getBean(CreateLoyaltyBankCommandInterceptor.class)
		);
		commandBus.registerDispatchInterceptor(
				context.getBean(UpdateBusinessCommandInterceptor.class)
		);
		commandBus.registerDispatchInterceptor(
				context.getBean(DeleteBusinessCommandInterceptor.class)
		);
		commandBus.registerDispatchInterceptor(
				context.getBean(CreateVoidTransactionCommandInterceptor.class)
		);
		commandBus.registerDispatchInterceptor(
				context.getBean(CreateCaptureTransactionCommandInterceptor.class)
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
	}
}
