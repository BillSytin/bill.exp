package chat.client_exe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SuppressWarnings("WeakerAccess")
@SpringBootApplication(scanBasePackages = "chat")
@EnableConfigurationProperties()
public class ClientExeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientExeApplication.class, args);
    }
}
