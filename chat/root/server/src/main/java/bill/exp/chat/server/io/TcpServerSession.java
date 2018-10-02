package bill.exp.chat.server.io;

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
public class TcpServerSession extends BaseAsyncSession implements ServerSession {
    @Autowired
    @Qualifier("queueExecutor")
    private TaskExecutor writeQueueExecutor;
    @Autowired
    @Qualifier("queueExecutor")
    private TaskExecutor processingQueueExecutor;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MessageProcessingManager processingManager;

    @Override
    protected TaskExecutor getWriteQueueExecutor() {
        return writeQueueExecutor;
    }

    @Override
    protected TaskExecutor getProcessingQueueExecutor() {
        return processingQueueExecutor;
    }

    @Override
    protected SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    protected MessageProcessingManager getProcessingManager() {
        return processingManager;
    }

    public TcpServerSession() {
        super(8192);
    }
}
