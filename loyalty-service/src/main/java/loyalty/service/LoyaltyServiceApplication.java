package loyalty.service;

import loyalty.service.command.interceptors.CreateAccountCommandInterceptor;
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
public class LoyaltyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoyaltyServiceApplication.class, args);
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
	}

	@Autowired
	public void configure(EventProcessingConfigurer configurer) {
		configurer.registerListenerInvocationErrorHandler("account-group",
				configuration -> new LoyaltyServiceEventsErrorHandler());
	}
}
