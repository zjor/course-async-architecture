package aa.auth.service;

import aa.auth.model.AuthUser;
import aa.common.events.auth.v1.AccountCreated;
import aa.common.events.auth.v1.AccountDeleted;
import aa.common.events.auth.v1.AccountRoleChanged;
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

    private void sendAsync(Object obj) {
        executor.submit(() ->
                producer.send(new ProducerRecord(topic, JSON.toJson(obj))));
    }

    public void sendAccountCreatedEventAsync(AuthUser user) {
        var event = AccountCreated.builder()
                .id(user.getId())
                .login(user.getLogin())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
        sendAsync(event);
    }

    public void sendAccountDeletedEventAsync(AuthUser user) {
        var event = AccountDeleted.builder()
                .id(user.getId())
                .deletedAt(user.getDeletedAt())
                .build();
        sendAsync(event);
    }

    public void setAccountRoleChangedEventAsync(AuthUser user) {
        var event = AccountRoleChanged.builder()
                .id(user.getId())
                .role(user.getRole())
                .build();
        sendAsync(event);
    }

}
