package chat.core.server.tasks;

import chat.core.tasks.BasePoolTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("serverPoolExecutor")
public class ServerPoolTaskExecutor extends BasePoolTaskExecutor {

    @Autowired
    public ServerPoolTaskExecutor(ServerPoolConfig config) {

        super("svr", config.getPoolSize());
    }
}
