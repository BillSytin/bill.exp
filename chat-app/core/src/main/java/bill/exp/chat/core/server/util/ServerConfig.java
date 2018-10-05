package bill.exp.chat.core.server.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@SuppressWarnings("unused")
@Configuration
public class ServerConfig {

    @Bean("inplaceExecutor")
    public TaskExecutor inplaceExecutor() {
        return Runnable::run;
    }
}
