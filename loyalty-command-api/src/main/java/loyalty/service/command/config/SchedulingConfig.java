package loyalty.service.command.config;

import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.quartz.QuartzDeadlineManager;
import org.axonframework.messaging.ScopeAwareProvider;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.axonframework.tracing.SpanFactory;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulingConfig {
    @Bean
    public DeadlineManager deadlineManager(
            final Scheduler jobScheduler,
            final org.axonframework.config.Configuration configuration,
            @Qualifier("eventSerializer") final Serializer serializer,
            final TransactionManager transactionManager,
            final SpanFactory spanFactory)
    {
        final ScopeAwareProvider scopeAwareProvider = new ConfigurationScopeAwareProvider(configuration);
        return QuartzDeadlineManager.builder()
                .scheduler(jobScheduler)
                .scopeAwareProvider(scopeAwareProvider)
                .serializer(serializer)
                .transactionManager(transactionManager)
                .spanFactory(spanFactory)
                .build();
    }
}
