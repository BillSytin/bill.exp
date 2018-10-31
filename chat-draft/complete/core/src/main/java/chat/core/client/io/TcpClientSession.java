package chat.core.client.io;

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
public class TcpClientSession extends BaseAsyncSession implements ClientSession {

    @Autowired
    public TcpClientSession(
            TcpClientConfig config,
            @Qualifier("clientSessionManager") SessionManager sessionManager,
            @Qualifier("clientMessageProcessingManager") MessageProcessingManager processingManager
    ) {

        super(config.getReadBufferSize(), sessionManager, processingManager);
    }
}
