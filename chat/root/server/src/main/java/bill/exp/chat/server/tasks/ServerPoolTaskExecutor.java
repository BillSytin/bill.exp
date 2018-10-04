package bill.exp.chat.server.tasks;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component("serverPoolExecutor")
public class ServerPoolTaskExecutor extends ThreadPoolTaskExecutor {

    public ServerPoolTaskExecutor() {
        setThreadNamePrefix("server_thread_pool_task_executor_thread");
        setCorePoolSize(40);
        initialize();
    }
}
