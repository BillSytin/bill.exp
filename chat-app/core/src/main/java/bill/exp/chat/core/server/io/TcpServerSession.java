package bill.exp.chat.core.server.io;

import bill.exp.chat.core.data.MessageProcessingManager;
import bill.exp.chat.core.io.BaseAsyncSession;
import bill.exp.chat.core.io.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Scope("prototype")
public class TcpServerSession extends BaseAsyncSession implements ServerSession {

    @Autowired
    public TcpServerSession(
            TcpServerConfig config,
            @Qualifier("serverSessionManager") SessionManager sessionManager,
            @Qualifier("serverMessageProcessingManager") MessageProcessingManager processingManager
    ) {

        super(config.getReadBufferSize(), sessionManager, processingManager);
    }
}
