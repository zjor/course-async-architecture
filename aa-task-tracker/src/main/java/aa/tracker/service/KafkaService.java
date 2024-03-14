package aa.tracker.service;

import aa.common.events.Event;
import aa.common.events.tasks.v1.TaskCompleted;
import aa.common.events.tasks.v2.TaskAssignedV2;
import aa.common.util.JSON;
import aa.tracker.model.Task;
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

    public static final String TASK_TOPIC = "tasks.tasks";

    private final AdminClient admin;
    private final KafkaProducer producer;

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public KafkaService(String bootstrapServers) {
        Properties config = new Properties();
        config.put("key.serializer", StringSerializer.class);
        config.put("value.serializer", StringSerializer.class);
        config.put("bootstrap.servers", bootstrapServers);

        admin = AdminClient.create(config);
        producer = new KafkaProducer(config);
    }


    public void ensureTopicAsync() {
        executor.submit(() -> {
            admin.createTopics(List.of(new NewTopic(TASK_TOPIC, 1, (short) 1)));
        });
    }

    private void sendAsync(Event<?> obj) {
        executor.submit(() ->
                producer.send(new ProducerRecord(TASK_TOPIC, JSON.toJson(obj))));
    }

    public void sendTaskAssignedEventAsync(Task task) {
        var data = TaskAssignedV2.builder()
                .taskId(task.getId())
                .jiraId(task.getJiraId())
                .assigneeId(task.getAssignee().getExtId())
                .assignmentFee(task.getAssignmentFee())
                .build();
        sendAsync(data.toEvent());
    }

    public void sendTaskCompletedEventAsync(Task task) {
        var data = TaskCompleted.builder()
                .taskId(task.getId())
                .assigneeId(task.getAssignee().getExtId())
                .reward(task.getReward())
                .build();
        sendAsync(data.toEvent());
    }

}
