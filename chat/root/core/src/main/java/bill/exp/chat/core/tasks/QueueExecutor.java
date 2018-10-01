package bill.exp.chat.core.tasks;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component("queueExecutor")
public class QueueExecutor extends SimpleAsyncTaskExecutor {

    public QueueExecutor() {
        setConcurrencyLimit(1);
        setThreadNamePrefix("queue_task_executor_thread");
    }
}
