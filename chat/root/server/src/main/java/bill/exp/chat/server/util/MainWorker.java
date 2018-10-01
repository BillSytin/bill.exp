package bill.exp.chat.server.util;

import bill.exp.chat.core.io.Channel;
import bill.exp.chat.core.util.Stoppable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component("mainWorker")
public class MainWorker implements Runnable, DisposableBean {
    @Autowired
    @Qualifier("inplaceExecutor")
    private TaskExecutor executor;

    @Autowired
    @Qualifier("tcpChannel")
    private Channel channel;

    @Autowired
    @Qualifier("mainLifetimeManager")
    private Stoppable lifeTimeManager;

    @Override
    public void run() {
        executor.execute(channel);
    }

    @Override
    public void destroy() throws Exception {

        lifeTimeManager.setIsStopping();

        if (channel instanceof DisposableBean)
            ((DisposableBean) channel).destroy();

    }
}
