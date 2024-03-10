package aa.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BillingApp {
    public static void main(String[] args) {
        var ctx = SpringApplication.run(BillingApp.class, args);
        var kafka = ctx.getBean(KafkaConsumerService.class);
        kafka.start();
    }
}
