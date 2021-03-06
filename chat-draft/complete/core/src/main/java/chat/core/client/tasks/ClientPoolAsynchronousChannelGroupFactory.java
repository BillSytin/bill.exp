package chat.core.client.tasks;

import chat.core.tasks.BasePoolAsynchronousChannelGroupFactory;
import chat.core.util.Stoppable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("clientPoolGroupFactory")
public class ClientPoolAsynchronousChannelGroupFactory extends BasePoolAsynchronousChannelGroupFactory {

    @Autowired
    public ClientPoolAsynchronousChannelGroupFactory(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("clientPoolExecutor") TaskExecutor poolExecutor
    ) {
        super(lifeTimeManager, poolExecutor);
    }
}
