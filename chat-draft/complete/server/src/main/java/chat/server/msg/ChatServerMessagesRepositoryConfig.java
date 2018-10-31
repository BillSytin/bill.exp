package chat.server.msg;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "server-messages")
public class ChatServerMessagesRepositoryConfig {

    @Min(5)
    @Max(1000)
    private int maxMessageCount;

    private int notificationTimeout;

    public ChatServerMessagesRepositoryConfig() {

        maxMessageCount = 100;
        notificationTimeout = 1000;
    }

    public int getNotificationTimeout() {
        return notificationTimeout;
    }

    public void setNotificationTimeout(int notificationTimeout) {
        this.notificationTimeout = notificationTimeout;
    }

    public int getMaxMessageCount() {

        return maxMessageCount;
    }

    public void setMaxMessageCount(int maxMessageCount) {

        this.maxMessageCount = maxMessageCount;
    }
}
