package chat.sample.perf;

import chat.core.io.Session;
import chat.core.model.ChatAction;
import chat.core.model.ChatClientEnvelope;
import chat.core.model.ChatServerEnvelope;
import chat.server.api.ChatServerRequestIntent;
import chat.server.api.ChatServerResponseIntent;
import chat.server.api.ChatServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Primary
@Profile("performance")
@Scope("prototype")
public class TestPerformanceChatServerService implements ChatServerService {

    private final TestPerformanceClientServer clientServer;

    @Autowired
    public TestPerformanceChatServerService(TestPerformanceClientServer clientServer) {

        this.clientServer = clientServer;
    }

    public TestPerformanceClientServer getClientServer() {
        return clientServer;
    }

    @Override
    public ChatServerResponseIntent process(Session session, ChatServerRequestIntent intent, ChatClientEnvelope[] models) {

        ChatAction action = ChatAction.Process;
        ChatServerEnvelope output = null;
        final ChatClientEnvelope model = models != null && models.length > 0 ? models[models.length - 1] : new ChatClientEnvelope();
        switch (intent.getAction()) {

            case OpenSession:
                clientServer.incServerAcceptCount();
                break;

            case CloseSession:
                clientServer.incServerOutputCloseCount();
                output = new ChatServerEnvelope();
                output.setAction(ChatAction.CloseSession);
                break;

            case Process:

                if (model.getAction() == ChatAction.CloseSession) {

                    clientServer.incServerInputCloseCount();
                    action = ChatAction.CloseSession;
                }
                else {

                    clientServer.incServerInputCount();
                    if (clientServer.checkCompletion()) {

                        output = new ChatServerEnvelope();
                        if (clientServer.cloneInputFirstMessage(model, output)) {
                            clientServer.incServerOutputCount();
                        } else {
                            output = null;
                        }
                    }
                }
                break;
        }

        return new ChatServerResponseIntent(action, output != null ? new ChatServerEnvelope[] { output } : null);
    }

    @Override
    public boolean isAsyncIntent(ChatServerRequestIntent intent) {

        return intent.getAction() == ChatAction.Process;
    }

    @Override
    public void dispose(Session session) {

    }
}
