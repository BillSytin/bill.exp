package chat.core.server.tasks;

import chat.core.tasks.BasePoolAsynchronousChannelGroupFactory;
import chat.core.util.Stoppable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("serverPoolGroupFactory")
public class ServerPoolAsynchronousChannelGroupFactory extends BasePoolAsynchronousChannelGroupFactory {

    @Autowired
    public ServerPoolAsynchronousChannelGroupFactory(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("serverPoolExecutor") TaskExecutor poolExecutor
    ) {
        super(lifeTimeManager, poolExecutor);
    }
}
