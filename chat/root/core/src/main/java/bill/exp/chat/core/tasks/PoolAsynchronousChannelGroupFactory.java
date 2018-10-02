package bill.exp.chat.core.tasks;

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

    private AsynchronousChannelGroup group;

    @Autowired
    @Qualifier("poolExecutor")
    private TaskExecutor poolExecutor;

    @PostConstruct
    private void init() throws IOException {
        group = AsynchronousChannelGroup.withThreadPool(((ThreadPoolTaskExecutor) poolExecutor).getThreadPoolExecutor());
    }

    @Override
    public AsynchronousChannelGroup getInstance() {
        return group;
    }
}
