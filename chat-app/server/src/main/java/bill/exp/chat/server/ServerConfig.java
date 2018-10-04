package bill.exp.chat.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import java.net.InetSocketAddress;

@Configuration
public class ServerConfig {

    @Bean("inplaceExecutor")
    public TaskExecutor inplaceExecutor() {
        return Runnable::run;
    }

    @Bean("serverAddress")
    public InetSocketAddress serverAddress() {
        return new InetSocketAddress("localhost", 7777);
    }
}
