package aa.tracker;

import aa.common.events.auth.v1.AccountCreated;
import aa.common.events.auth.EventType;
import aa.common.util.JSON;
import aa.tracker.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
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
        for (ConsumerRecord<String, String> msg: records) {
            var value = msg.value();
            log.info("<= {}", value);
            if (value.contains(EventType.ACCOUNT_CREATED.name())) {
                handleAccountCreatedEvent(JSON.fromJson(value, AccountCreated.class));
            }
        }
    }

    private void handleAccountCreatedEvent(AccountCreated event) {
        accountRepository.ensure(event.getId(), event.getLogin(), event.getRole());

    }

}
