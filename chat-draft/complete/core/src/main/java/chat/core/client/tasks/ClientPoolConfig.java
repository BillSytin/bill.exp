package chat.core.client.tasks;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "client-pool")
public class ClientPoolConfig {

    @Min(3)
    @Max(50)
    private int poolSize;

    public ClientPoolConfig() {

        poolSize = 3;
    }

    public int getPoolSize() {

        return poolSize;
    }

    public void setPoolSize(int poolSize){

        this.poolSize = poolSize;
    }
}
