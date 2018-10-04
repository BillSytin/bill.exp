package bill.exp.chat.server.tasks;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "server-pool")
public class ServerPoolConfig {

    private int poolSize;

    public ServerPoolConfig() {
        poolSize = 40;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
