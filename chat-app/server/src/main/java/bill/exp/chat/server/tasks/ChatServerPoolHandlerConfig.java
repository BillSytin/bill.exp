package bill.exp.chat.server.tasks;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "server-handler-pool")
public class ChatServerPoolHandlerConfig {

    @Min(5)
    @Max(100)
    private int poolSize;

    public ChatServerPoolHandlerConfig() {
        poolSize = 20;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {

        this.poolSize = poolSize;
    }
}
