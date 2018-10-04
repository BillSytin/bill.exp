package bill.exp.chat.server.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component("serverPoolExecutor")
public class ServerPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Autowired
    public ServerPoolTaskExecutor(ServerPoolConfig config) {

        setThreadNamePrefix("server_thread_pool_task_executor_thread");
        setCorePoolSize(config.getPoolSize());
        initialize();
    }
}
