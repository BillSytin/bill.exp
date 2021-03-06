package chat.core.server.util;

import chat.core.io.Channel;
import chat.core.util.Stoppable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("mainWorker")
@Profile({"server", "sample"})
public class MainWorker implements Runnable, Stoppable {

    private final TaskExecutor executor;
    private final Channel channel;
    private final Stoppable lifeTimeManager;

    @Autowired
    public MainWorker(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("inplaceExecutor") TaskExecutor executor,
            @Qualifier("tcpAcceptChannel") Channel channel
    ) {

        this.lifeTimeManager = lifeTimeManager;
        this.executor = executor;
        this.channel = channel;
    }

    @Override
    public String toString() {

        return channel.toString();
    }

    @Override
    public void run() {

        executor.execute(channel);
    }

    @Override
    public boolean isStopping() {

        return lifeTimeManager.isStopping();
    }

    @Override
    public void setStopping() {

        lifeTimeManager.setStopping();

        if (channel instanceof Stoppable) {
            ((Stoppable) channel).setStopping();
        }
    }

    @Override
    public void setStopped() {

        lifeTimeManager.setStopped();
    }

    @Override
    public boolean waitStopped(int timeout) {

        return lifeTimeManager.waitStopped(timeout);
    }
}
