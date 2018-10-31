package chat.core.server.io;

import chat.core.data.MessageProcessingManager;
import chat.core.io.BaseAsyncSession;
import chat.core.io.SessionManager;
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
