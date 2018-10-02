package bill.exp.chat.core.tasks;

import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component("queueExecutor")
@Scope("prototype")
public class QueueTaskExecutor extends SimpleAsyncTaskExecutor {

    public QueueTaskExecutor() {
        super("queue_task_executor_thread");
        setConcurrencyLimit(1);
    }
}
