package bill.exp.chat.server.util;

import bill.exp.chat.core.io.Channel;
import bill.exp.chat.core.util.Stoppable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("mainWorker")
public class MainWorker implements Runnable, Stoppable, DisposableBean {

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
    public void run() {

        executor.execute(channel);
    }

    @Override
    public void destroy() {

        setIsStopping();

    }

    @Override
    public boolean getIsStopping() {

        return lifeTimeManager.getIsStopping();
    }

    @Override
    public void setIsStopping() {

        lifeTimeManager.setIsStopping();

        if (channel instanceof Stoppable) {
            ((Stoppable) channel).setIsStopping();
        }
    }

    @Override
    public void setIsStopped() {

        lifeTimeManager.setIsStopped();
    }

    @Override
    public boolean waitStopped(int timeout) {

        return lifeTimeManager.waitStopped(timeout);
    }
}
