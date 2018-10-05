package bill.exp.chat.server.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("serverPoolHandlerExecutor")
public class ChatServerPoolHandlerExecutor extends ThreadPoolTaskExecutor {

    @Autowired
    public ChatServerPoolHandlerExecutor(ChatServerPoolHandlerConfig config) {

        setThreadNamePrefix("svr_han");
        setCorePoolSize(config.getPoolSize());
        initialize();
    }
}
