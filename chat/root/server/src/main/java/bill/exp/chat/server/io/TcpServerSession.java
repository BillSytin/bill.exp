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
    private final TaskExecutor writeQueueExecutor;
    private final TaskExecutor processingQueueExecutor;
    private final SessionManager sessionManager;
    private final MessageProcessingManager processingManager;

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

    @Autowired
    public TcpServerSession(
            @Qualifier("queueExecutor") TaskExecutor writeQueueExecutor,
            @Qualifier("queueExecutor") TaskExecutor processingQueueExecutor,
            @Qualifier("serverSessionManager") SessionManager sessionManager,
            @Qualifier("serverMessageProcessingManager") MessageProcessingManager processingManager
    ) {

        super(8192);
        this.writeQueueExecutor = writeQueueExecutor;
        this.processingQueueExecutor = processingQueueExecutor;
        this.sessionManager = sessionManager;
        this.processingManager = processingManager;
    }
}
