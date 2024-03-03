package aa.auth.service;

import aa.auth.model.AuthUser;
import aa.common.events.auth.AccountCreated;
import aa.common.util.JSON;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaService {

    private final String topic;
    private final AdminClient admin;
    private final KafkaProducer producer;

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public KafkaService(String bootstrapServers, String topic) {
        this.topic = topic;
        Properties config = new Properties();
        config.put("key.serializer", StringSerializer.class);
        config.put("value.serializer", StringSerializer.class);
        config.put("bootstrap.servers", bootstrapServers);

        admin = AdminClient.create(config);
        producer = new KafkaProducer(config);
    }


    public void ensureTopicAsync() {
        executor.submit(() -> {
            admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1)));
        });
    }

    public void sendAccountCreatedEventAsync(AuthUser user) {
        var event = AccountCreated.builder()
                .id(user.getId())
                .login(user.getLogin())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
        executor.submit(() ->
                producer.send(new ProducerRecord(topic, JSON.toJson(event))));
    }

    public void sendAccountDeletedEventAsync(AuthUser user) {

    }

    public void setAccountRoleChangedEventAsync(AuthUser user) {

    }

}
