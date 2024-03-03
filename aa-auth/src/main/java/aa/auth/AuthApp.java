package aa.auth;

import aa.auth.service.KafkaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApp {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(AuthApp.class, args);
        var kafka = ctx.getBean(KafkaService.class);
        kafka.ensureTopicAsync();
    }
}
