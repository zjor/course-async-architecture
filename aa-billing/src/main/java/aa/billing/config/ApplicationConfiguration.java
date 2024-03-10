package aa.billing.config;

import aa.billing.KafkaConsumerService;
import aa.billing.repository.AccountRepository;
import aa.billing.repository.AuditLogRepository;
import aa.billing.repository.BalanceRepository;
import aa.billing.service.AccountService;
import aa.billing.service.BillingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public BillingService billingService(
            AccountRepository accountRepository,
            BalanceRepository balanceRepository,
            AuditLogRepository auditLogRepository) {
        return new BillingService(accountRepository, balanceRepository, auditLogRepository);
    }

    @Bean
    public KafkaConsumerService kafkaConsumerService(
            @Value("${kafka.servers}") String servers,
            @Value("${kafka.topics}") List<String> topics,
            @Value("${kafka.groupId}") String groupId,
            AccountService accountService,
            AccountRepository accountRepository,
            BillingService billingService) {
        return new KafkaConsumerService(servers, groupId, topics, accountService, accountRepository, billingService);
    }

    @Bean
    public AccountService accountService(
            AccountRepository accountRepository,
            BalanceRepository balanceRepository
    ) {
        return new AccountService(accountRepository, balanceRepository);
    }

}
