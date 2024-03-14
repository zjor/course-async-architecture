package aa.billing;

import aa.billing.repository.AccountRepository;
import aa.billing.service.AccountService;
import aa.billing.service.BillingService;
import aa.common.events.Event;
import aa.common.events.SchemaValidator;
import aa.common.events.auth.v1.AccountCreated;
import aa.common.events.auth.v1.AccountDeleted;
import aa.common.events.auth.v1.AccountRoleChanged;
import aa.common.events.tasks.v1.TaskAssignedV1;
import aa.common.events.tasks.v1.TaskCompleted;
import aa.common.events.tasks.v2.TaskAssignedV2;
import aa.common.util.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class KafkaConsumerService {

    private final List<String> topics;
    private final KafkaConsumer<String, String> consumer;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final BillingService billingService;

    public KafkaConsumerService(
            String servers,
            String groupId,
            List<String> topics,
            AccountService accountService,
            AccountRepository accountRepository,
            BillingService billingService) {
        this.topics = topics;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.billingService = billingService;

        Properties properties = new Properties();
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", StringDeserializer.class);
        properties.put("bootstrap.servers", servers);
        properties.put("group.id", groupId);
        properties.put("auto.offset.reset", "earliest");
        consumer = new KafkaConsumer<>(properties);
    }

    public void start() {
        consumer.subscribe(topics);
        (new Thread(() -> {
            while (true) {
                loop();
            }
        }
        )).start();
    }

    private void loop() {
        var records = consumer.poll(Duration.ofMillis(15000L));
        for (ConsumerRecord<String, String> msg : records) {
            var value = msg.value();
            log.info("<= {}", value);

            try {
                var event = JSON.fromJson(value, new TypeReference<Event<Map>>() {
                });
                var schemaKey = event.getSchemaKey();
                var jsonData = JSON.toJson(event.getData());

                switch (schemaKey) {
                    case AccountCreated.SCHEMA -> handleAccountCreatedEvent(jsonData);
                    case AccountDeleted.SCHEMA -> handleAccountDeletedEvent(jsonData);
                    case AccountRoleChanged.SCHEMA -> handleAccountRoleChangedEvent(jsonData);
                    case TaskAssignedV1.SCHEMA -> handleTaskAssignedV1Event(jsonData);
                    case TaskAssignedV2.SCHEMA -> handleTaskAssignedV2Event(jsonData);
                    case TaskCompleted.SCHEMA -> handleTaskCompletedEvent(jsonData);
                }
            } catch (Exception e) {
                log.error("Failed to handle message: " + value, e);
            }
        }
    }

    private void handleAccountCreatedEvent(String json) {
        if (SchemaValidator.isValid(json, AccountCreated.SCHEMA)) {
            var event = JSON.fromJson(json, AccountCreated.class);
            accountService.ensure(event.getId(), event.getRole());
        }
    }

    private void handleAccountDeletedEvent(String json) {
        if (SchemaValidator.isValid(json, AccountDeleted.SCHEMA)) {
            var event = JSON.fromJson(json, AccountDeleted.class);
            accountRepository.setDeletedAt(event.getId(), event.getDeletedAt());
        }
    }

    private void handleAccountRoleChangedEvent(String json) {
        if (SchemaValidator.isValid(json, AccountRoleChanged.SCHEMA)) {
            var event = JSON.fromJson(json, AccountRoleChanged.class);
            accountRepository.setRole(event.getId(), event.getRole());
        }
    }

    private void handleTaskAssignedV1Event(String json) {
        if (SchemaValidator.isValid(json, TaskAssignedV1.SCHEMA)) {
            var event = JSON.fromJson(json, TaskAssignedV1.class);
            billingService.handleTaskAssigned(event.getAssigneeId(), event.getAssignmentFee(), event.getTaskId());
        }
    }

    private void handleTaskAssignedV2Event(String json) {
        if (SchemaValidator.isValid(json, TaskAssignedV2.SCHEMA)) {
            var event = JSON.fromJson(json, TaskAssignedV2.class);
            billingService.handleTaskAssigned(event.getAssigneeId(), event.getAssignmentFee(), event.getTaskId());
        }
    }

    private void handleTaskCompletedEvent(String json) {
        if (SchemaValidator.isValid(json, TaskCompleted.SCHEMA)) {
            var event = JSON.fromJson(json, TaskCompleted.class);
            billingService.handleTaskCompleted(event.getAssigneeId(), event.getReward(), event.getTaskId());
        }
    }

}
