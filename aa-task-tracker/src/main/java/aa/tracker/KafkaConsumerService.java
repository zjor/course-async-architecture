package aa.tracker;

import aa.common.events.Event;
import aa.common.events.SchemaValidator;
import aa.common.events.auth.v1.AccountCreated;
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
            if (AccountCreated.SCHEMA.equals(schemaKey)) {
                var json = JSON.toJson(event.getData());
                if (SchemaValidator.isValid(json, AccountCreated.SCHEMA)) {
                    handleAccountCreatedEvent(JSON.fromJson(json, AccountCreated.class));
                }
            }
        }
    }

    private void handleAccountCreatedEvent(AccountCreated event) {
        accountRepository.ensure(event.getId(), event.getLogin(), event.getRole());
    }

}
