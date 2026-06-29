package com.bits.member.application.config;

import com.bits.ddd.infra.core.context.TenantContext;
import com.bits.ddd.infra.core.config.TenantContextFilter;
import java.util.concurrent.Executor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TenantContextConfig {

    @Bean
    public FilterRegistrationBean<TenantContextFilter> tenantContextFilter() {
        FilterRegistrationBean<TenantContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantContextFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    @Bean
    @Primary
    public Executor taskExecutor(TaskDecorator tenantTaskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("tenant-task-");
        executor.setTaskDecorator(tenantTaskDecorator);
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskDecorator tenantTaskDecorator() {
        return runnable -> {
            String tenantId = TenantContext.getTenantId();
            return () -> {
                try {
                    if (tenantId != null) {
                        TenantContext.setTenantId(tenantId);
                    }
                    runnable.run();
                } finally {
                    TenantContext.clear();
                }
            };
        };
    }
}
