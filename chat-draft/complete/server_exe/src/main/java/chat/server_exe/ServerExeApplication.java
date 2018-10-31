package chat.server_exe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SuppressWarnings("WeakerAccess")
@SpringBootApplication(scanBasePackages = "chat")
@EnableConfigurationProperties()
public class ServerExeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerExeApplication.class, args);
    }
}
