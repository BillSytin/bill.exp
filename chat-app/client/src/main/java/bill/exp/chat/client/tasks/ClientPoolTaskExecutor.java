package bill.exp.chat.client.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component("clientPoolExecutor")
public class ClientPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Autowired
    public ClientPoolTaskExecutor(ClientPoolConfig config) {

        setThreadNamePrefix("client_thread_pool_task_executor_thread");
        setCorePoolSize(config.getPoolSize());
        initialize();
    }
}
