package bill.exp.chat.client.api;

import bill.exp.chat.client.console.ChatClientConsole;
import bill.exp.chat.core.api.ResponseIntent;
import bill.exp.chat.core.data.Message;
import bill.exp.chat.core.data.ResponseIntentMessage;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.util.Stoppable;
import bill.exp.chat.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Service
@Scope("prototype")
public class DefaultChatClientService implements ChatClientService, ConsoleChatClientService, Stoppable {

    private final Log logger = LogFactory.getLog(getClass());
    private final ChatClientConsole console;
    private final ObjectFactory<TaskExecutor> taskExecutorObjectFactory;
    private long messagesStamp;
    private Session session;
    private String authToken;
    private CompletableFuture<String> authRequestFuture;
    private volatile boolean isReading;
    private volatile boolean isStopping;
    private volatile boolean isStopped;

    @Autowired
    public DefaultChatClientService(
            ChatClientConsole console,
            @Qualifier("clientPoolExecutor") ObjectFactory<TaskExecutor> taskExecutorObjectFactory
    ) {

        this.taskExecutorObjectFactory = taskExecutorObjectFactory;
        this.console = console;
        this.messagesStamp = 0;
        this.session = null;
    }

    @Override
    public ChatClientConsole getConsole() {

        return console;
    }

    public synchronized Session getSession() {

        return session;
    }

    public synchronized void setSession(Session session) {

        this.session = session;
    }

    private synchronized String getAuthToken() {

        return authToken;
    }

    private synchronized void setAuthToken(String authToken) {

        this.authToken = authToken;
    }

    private synchronized CompletableFuture<String> getAuthRequestFuture() {

        return authRequestFuture;
    }

    private synchronized void setAuthRequestFuture(CompletableFuture<String> authRequestFuture) {

        this.authRequestFuture = authRequestFuture;
    }

    private Log getLogger() {

        return logger;
    }

    private void readConsole() {

        try {

            while (!isStopping()) {

                final ChatMessage inputMesssage = console.readInput();
                if (inputMesssage == null)
                    break;

                sendMessage(inputMesssage);
                final Future<String> requestFuture = getAuthRequestFuture();
                if (requestFuture != null) {

                    try {

                        requestFuture.get(5, TimeUnit.MINUTES);
                    } catch (final Exception e) {

                        getLogger().error("Login is not completed at the expected time", e);
                    }
                }
            }
        }
        finally {

            isReading = false;
            setStopped();
        }
    }

    private void sendMessage(ChatMessage message) {

        if (message == null) {

            return;
        }

        if (getSession() == null) {

            final ChatMessage errorMessage = new ChatMessage();
            errorMessage.setRoute(ChatStandardRoute.Error.toString());
            errorMessage.setAction(ChatStandardAction.Default.toString());
            errorMessage.setStatus(ChatStandardStatus.Failed.toString());
            errorMessage.setContent(message.getContent());

            receiveMessage(message);
            return;
        }

        if (StringUtils.isEmpty(getAuthToken()) && !ChatStandardRoute.Help.toString().equals(message.getRoute()) ) {

            final Future<String> authRequestFuture = this.getAuthRequestFuture();
            if (authRequestFuture != null) {

                try {

                    final String token = authRequestFuture.get(5, TimeUnit.MINUTES);
                    this.setAuthRequestFuture(null);
                }
                catch (final Exception e) {

                    final ChatMessage errorMessage = ChatMessage.createErrorMessage(e);
                    receiveMessage(errorMessage);
                }
                this.setAuthRequestFuture(null);
            }
            else {

                message.setRoute(ChatStandardRoute.Auth.toString());
                message.setAction(ChatStandardAction.Login.toString());
                this.setAuthRequestFuture(new CompletableFuture<>());
            }
        }

        sendEnvelopeWithMessage(message);
    }

    private void sendEnvelopeWithMessage(ChatMessage message) {

        final ChatClientEnvelope content = new ChatClientEnvelope();
        content.setAuthToken(getAuthToken());
        content.setMessages(new ChatMessageList());
        content.getMessages().add(message);
        final ResponseIntent responseIntent = new ChatClientResponseIntent(
                ChatAction.Process,
                new ChatClientEnvelope[] { content });
        final Message intentMessage = new ResponseIntentMessage(responseIntent);

        getSession().submit(intentMessage);
    }

    private void updateMessages(long stamp) {

        if (stamp > messagesStamp && getSession() != null) {

            messagesStamp = stamp;
        }
    }

    private void receiveMessage(ChatMessage message) {

        if (ChatStandardRoute.Message.toString().equals(message.getRoute())) {

            if (ChatStandardAction.Notify.toString().equals(message.getAction())) {

                long stamp = 0;
                try {

                    stamp = Long.parseLong(message.getContent());
                }
                catch (final NumberFormatException e) {

                    getLogger().error(String.format("Invalid stamp %s%n", message.getContent()), e);
                }

                if (stamp != 0) {

                    updateMessages(stamp);
                }
            }
        }
        else if (ChatStandardRoute.Session.toString().equals(message.getRoute())) {

            console.printOutput(message);
            if (ChatStandardAction.Open.toString().equals(message.getAction())) {

                final ChatMessage welcomeMessage = new ChatMessage();
                welcomeMessage.setRoute(ChatStandardRoute.Help.toString());
                welcomeMessage.setAction(ChatStandardAction.Welcome.toString());
                welcomeMessage.setContent("");
                sendMessage(welcomeMessage);
            }
            else if (ChatStandardAction.Close.toString().equals(message.getAction())) {

                setStopping();
            }
        }
        else if (ChatStandardRoute.Help.toString().equals(message.getRoute())) {

            console.printOutput(message);
            if (ChatStandardAction.Welcome.toString().equals(message.getAction())) {
                if (!isReading) {

                    isReading = true;
                    taskExecutorObjectFactory.getObject().execute(this::readConsole);
                }
            }
        }
        else if (ChatStandardRoute.Auth.toString().equals(message.getRoute())) {

            String authToken = getAuthToken();
            final CompletableFuture<String> authRequestFuture = this.getAuthRequestFuture();
            if (ChatStandardAction.Login.toString().equals(message.getAction())) {
                if (ChatStandardStatus.Success.toString().equals(message.getStatus())) {

                    authToken = message.getContent();
                    message.setContent(null);
                    setAuthToken(authToken);
                }
            }
            console.printOutput(message);

            if (authRequestFuture != null) {

                authRequestFuture.complete(authToken);
            }
        }
        else if (ChatStandardRoute.Error.toString().equals(message.getRoute())) {

            final CompletableFuture<String> authRequestFuture = this.getAuthRequestFuture();
            if (authRequestFuture != null) {

                authRequestFuture.complete(null);
            }
            console.printOutput(message);
        }
        else {

            console.printOutput(message);
        }
    }

    @Override
    public synchronized ChatClientResponseIntent process(Session session, ChatClientRequestIntent intent, ChatServerEnvelope[] models) {

        setSession(session);
        if (intent.getAction() == ChatAction.OpenSession) {

            final ChatMessage message = new ChatMessage();
            message.setRoute(ChatStandardRoute.Session.toString());
            message.setAction(ChatStandardAction.Open.toString());
            message.setContent(session.toString());
            receiveMessage(message);
        }

        if (models != null) {

            for (ChatServerEnvelope model : models) {

                if (model != null && model.getMessages() != null) {

                    for (ChatMessage message : model.getMessages()) {

                        try {

                            receiveMessage(message);
                        } catch (final Exception e) {

                            getLogger().error(String.format("Error processing message %s%n", message.getContent()), e);
                        }
                    }

                }
            }
        }

        if (intent.getAction() == ChatAction.CloseSession) {

            final ChatMessage message = new ChatMessage();
            message.setRoute(ChatStandardRoute.Session.toString());
            message.setAction(ChatStandardAction.Close.toString());
            message.setContent(session.toString());
            receiveMessage(message);
        }

        return null;
    }

    @Override
    public boolean isAsyncIntent(ChatClientRequestIntent intent) {

        return intent.getAction() == ChatAction.Process;
    }

    @Override
    public boolean isStopping() {

        return isStopping;
    }

    @Override
    public void setStopping() {

        isStopping = true;
        if (console instanceof Stoppable) {

            ((Stoppable) console).setStopping();
        }
    }

    @Override
    public void setStopped() {

        isStopped = true;
    }

    @Override
    public boolean waitStopped(int timeout) {

        return isStopped;
    }
}
