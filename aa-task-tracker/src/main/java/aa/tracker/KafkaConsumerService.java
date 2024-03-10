package aa.tracker;

import aa.common.events.Event;
import aa.common.events.SchemaValidator;
import aa.common.events.auth.v1.AccountCreated;
import aa.common.events.auth.v1.AccountDeleted;
import aa.common.events.auth.v1.AccountRoleChanged;
import aa.common.util.JSON;
import aa.tracker.repository.AccountRepository;
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

    private final String topic;
    private final KafkaConsumer<String, String> consumer;
    private final AccountRepository accountRepository;

    public KafkaConsumerService(
            String servers,
            String groupId,
            String topic,
            AccountRepository accountRepository) {
        this.topic = topic;
        this.accountRepository = accountRepository;

        Properties properties = new Properties();
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", StringDeserializer.class);
        properties.put("bootstrap.servers", servers);
        properties.put("group.id", groupId);
        properties.put("auto.offset.reset", "earliest");
        consumer = new KafkaConsumer<>(properties);
    }

    public void start() {
        consumer.subscribe(List.of(topic));
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

            var event = JSON.fromJson(value, new TypeReference<Event<Map>>() {});
            var schemaKey = event.getSchemaKey();
            var jsonData = JSON.toJson(event.getData());

            switch (schemaKey) {
                case AccountCreated.SCHEMA -> handleAccountCreatedEvent(jsonData);
                case AccountDeleted.SCHEMA -> handleAccountDeletedEvent(jsonData);
                case AccountRoleChanged.SCHEMA -> handleAccountRoleChangedEvent(jsonData);
            }
        }
    }

    private void handleAccountCreatedEvent(String json) {
        if (SchemaValidator.isValid(json, AccountCreated.SCHEMA)) {
            var event = JSON.fromJson(json, AccountCreated.class);
            accountRepository.ensure(event.getId(), event.getLogin(), event.getRole());
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

}
