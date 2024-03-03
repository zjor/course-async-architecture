package aa.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrackerApp {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(TrackerApp.class, args);
        var kafka = ctx.getBean(KafkaConsumerService.class);
        kafka.start();
    }

}
