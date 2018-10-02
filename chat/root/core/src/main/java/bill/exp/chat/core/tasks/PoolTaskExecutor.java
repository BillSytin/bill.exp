package bill.exp.chat.core.tasks;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component("poolExecutor")
public class PoolTaskExecutor extends ThreadPoolTaskExecutor {

    public PoolTaskExecutor() {
        setThreadNamePrefix("thread_pool_task_executor_thread");
        setCorePoolSize(40);
        initialize();
    }
}
