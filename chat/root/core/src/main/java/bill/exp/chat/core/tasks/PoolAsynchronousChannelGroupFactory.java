package bill.exp.chat.core.tasks;

import bill.exp.chat.core.util.Stoppable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;

@Component
public class PoolAsynchronousChannelGroupFactory implements AsynchronousChannelGroupFactory {

    private final Stoppable lifeTimeManager;
    private final AsynchronousChannelGroup group;

    @Autowired
    public PoolAsynchronousChannelGroupFactory(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("poolExecutor") TaskExecutor poolExecutor
    ) {

        this.lifeTimeManager = lifeTimeManager;
        this.group = createGroup(poolExecutor);
    }

    private AsynchronousChannelGroup createGroup(TaskExecutor poolExecutor) {

        try {
            return AsynchronousChannelGroup.withThreadPool(((ThreadPoolTaskExecutor) poolExecutor).getThreadPoolExecutor());
        }
        catch (final IOException e) {
            lifeTimeManager.setIsStopping();
        }

        return null;
    }

    @Override
    public AsynchronousChannelGroup getInstance() {

        return group;
    }
}
