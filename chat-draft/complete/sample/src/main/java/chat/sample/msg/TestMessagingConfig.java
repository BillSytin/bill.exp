package chat.sample.msg;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "test.messaging")
public class TestMessagingConfig {
    @Min(1)
    @Max(50)
    private int clientCount;

    public TestMessagingConfig() {

        clientCount = 30;
    }

    public int getClientCount() {
        return clientCount;
    }

    public void setClientCount(int clientCount) {
        this.clientCount = clientCount;
    }
}
