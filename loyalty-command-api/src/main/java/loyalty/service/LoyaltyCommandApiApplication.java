package loyalty.service;

import loyalty.service.command.interceptors.CreateAccountCommandInterceptor;
import loyalty.service.command.interceptors.CreateLoyaltyBankCommandInterceptor;
import loyalty.service.command.interceptors.DeleteAccountCommandInterceptor;
import loyalty.service.command.interceptors.UpdateAccountCommandInterceptor;
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
	}

	@Autowired
	public void configure(EventProcessingConfigurer configurer) {
		// TODO: Save group strings to constants
		configurer.registerListenerInvocationErrorHandler("account-group",
				configuration -> new LoyaltyServiceEventsErrorHandler());
		configurer.registerListenerInvocationErrorHandler("loyalty-bank-group",
				configuration -> new LoyaltyServiceEventsErrorHandler());
	}
}
