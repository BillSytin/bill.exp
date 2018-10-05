package bill.exp.chat.core.tasks;

import bill.exp.chat.core.util.Stoppable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ExecutorService;

public class BasePoolAsynchronousChannelGroupFactory implements AsynchronousChannelGroupFactory {

    protected final Log logger = LogFactory.getLog(getClass());

    private final Stoppable lifeTimeManager;
    private final AsynchronousChannelGroup group;

    public BasePoolAsynchronousChannelGroupFactory(
            Stoppable lifeTimeManager,
            TaskExecutor poolExecutor
    ) {

        this.lifeTimeManager = lifeTimeManager;
        this.group = createGroup(poolExecutor);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public String toString() {

        return super.toString();
    }

    private AsynchronousChannelGroup createGroup(TaskExecutor poolExecutor) {

        try {
            ExecutorService service = poolExecutor instanceof ExecutorService ? ((ExecutorService) poolExecutor) :
                    poolExecutor instanceof ThreadPoolTaskExecutor ? ((ThreadPoolTaskExecutor) poolExecutor).getThreadPoolExecutor() : null;

            return AsynchronousChannelGroup.withThreadPool(service);
        }
        catch (final IOException e) {

            logger.error(String.format("Error creating channel group: %s%n", this.toString()), e);
            lifeTimeManager.setStopping();
        }

        return null;
    }

    @Override
    public AsynchronousChannelGroup getInstance() {

        return group;
    }
}
