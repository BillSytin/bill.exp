package chat.client_exe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "chat")
@EnableConfigurationProperties()
class ClientExeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientExeApplication.class, args);
    }
}
