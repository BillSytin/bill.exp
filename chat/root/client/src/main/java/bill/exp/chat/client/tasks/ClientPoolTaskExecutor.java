package bill.exp.chat.client.tasks;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component("clientPoolExecutor")
public class ClientPoolTaskExecutor extends ThreadPoolTaskExecutor {

    public ClientPoolTaskExecutor() {
        setThreadNamePrefix("client_thread_pool_task_executor_thread");
        setCorePoolSize(40);
        initialize();
    }
}
