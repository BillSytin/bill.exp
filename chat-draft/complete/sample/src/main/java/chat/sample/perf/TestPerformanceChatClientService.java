package chat.sample.perf;

import chat.client.api.ChatClientRequestIntent;
import chat.client.api.ChatClientResponseIntent;
import chat.client.api.ChatClientService;
import chat.core.io.Session;
import chat.core.model.ChatAction;
import chat.core.model.ChatClientEnvelope;
import chat.core.model.ChatServerEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unused", "EmptyMethod"})
@Component
@Primary
@Profile("performance")
@Scope("prototype")
public class TestPerformanceChatClientService implements ChatClientService {

    private final TestPerformanceClientServer clientServer;

    @Autowired
    public TestPerformanceChatClientService(TestPerformanceClientServer clientServer) {

        this.clientServer = clientServer;
    }

    public TestPerformanceClientServer getClientServer() {
        return clientServer;
    }

    @Override
    public ChatClientResponseIntent process(Session session, ChatClientRequestIntent intent, ChatServerEnvelope[] models) {

        ChatAction action = ChatAction.Process;
        ChatClientEnvelope output = null;
        final ChatServerEnvelope model = models != null && models.length > 0 ? models[models.length - 1] : new ChatServerEnvelope();
        switch (intent.getAction()) {

            case OpenSession:
                clientServer.incClientConnectCount();
                // clientServer.incClientOutputCount();
                // output = clientServer.generateOpenSessionResponse(clientServer.generateMessageString());
                break;

            case CloseSession:
                clientServer.incClientOutputCloseCount();
                output = new ChatClientEnvelope();
                output.setAction(ChatAction.CloseSession);
                break;

            case Process:
                if (model.getAction() == ChatAction.CloseSession) {

                    clientServer.incClientInputCloseCount();
                    action = ChatAction.CloseSession;
                }
                else {

                    clientServer.incClientInputCount();
                    if (clientServer.checkCompletion()) {

                        output = new ChatClientEnvelope();
                        if (clientServer.cloneInputFirstMessage(model, output)) {

                            clientServer.incClientOutputCount();
                            if (clientServer.getClientSleepTime() > 0) {
                                try {
                                    Thread.sleep(clientServer.getRandomSleepTime());
                                }
                                catch (final InterruptedException ignored) {
                                }
                            }
                        } else {
                            output = null;
                        }
                    }
                }
                break;
        }

        return new ChatClientResponseIntent(action, output != null ? new ChatClientEnvelope[] { output } : null);
    }

    @Override
    public boolean isAsyncIntent(ChatClientRequestIntent intent) {

        return false;
    }

    @Override
    public void dispose(Session session) {

    }
}
