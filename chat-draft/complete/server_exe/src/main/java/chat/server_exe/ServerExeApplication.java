package chat.server_exe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "chat")
@EnableConfigurationProperties()
class ServerExeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerExeApplication.class, args);
    }
}
