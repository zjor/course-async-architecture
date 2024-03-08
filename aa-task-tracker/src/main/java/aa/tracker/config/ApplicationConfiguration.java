package aa.tracker.config;

import aa.common.ext.spring.aop.LoggingAspect;
import aa.tracker.KafkaConsumerService;
import aa.tracker.auth.AuthFilter;
import aa.tracker.auth.AuthServerClient;
import aa.tracker.repository.AccountRepository;
import aa.tracker.service.TaskAssignmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ApplicationConfiguration {

    @Bean
    @Order(0)
    public ObjectMapper objectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

    @Bean
    public AuthServerClient authServerClient(@Value("${auth.baseUrl}") String baseUrl) {
        return new AuthServerClient(baseUrl);
    }

    @Bean
    public TaskAssignmentService taskAssignmentService(AccountRepository accountRepository) {
        return new TaskAssignmentService(accountRepository);
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistrationBean(
            AuthServerClient authServerClient,
            AccountRepository accountRepository) {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter(authServerClient, accountRepository));
        registrationBean.addUrlPatterns("/api/v1/*");
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public KafkaConsumerService kafkaConsumerService(
            @Value("${kafka.servers}") String servers,
            @Value("${kafka.topic}") String topic,
            @Value("${kafka.groupId}") String groupId,
            AccountRepository accountRepository) {
        return new KafkaConsumerService(servers, groupId, topic, accountRepository);
    }

}
