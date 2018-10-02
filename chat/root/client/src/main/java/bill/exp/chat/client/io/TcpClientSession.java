package bill.exp.chat.client.io;

import bill.exp.chat.core.data.MessageProcessingManager;
import bill.exp.chat.core.io.BaseAsyncSession;
import bill.exp.chat.core.io.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TcpClientSession extends BaseAsyncSession implements ClientSession {
    @Autowired
    @Qualifier("queueExecutor")
    private TaskExecutor queueExecutor;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MessageProcessingManager processingManager;

    @Override
    protected TaskExecutor getQueueExecutor() {
        return queueExecutor;
    }

    @Override
    protected SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    protected MessageProcessingManager getProcessingManager() {
        return processingManager;
    }

    public TcpClientSession() {
        super(8192);
    }
}
