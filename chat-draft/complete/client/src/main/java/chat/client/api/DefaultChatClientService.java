package chat.client.api;

import chat.client.console.ChatClientConsole;
import chat.core.api.ResponseIntent;
import chat.core.data.Message;
import chat.core.data.ResponseIntentMessage;
import chat.core.io.Session;
import chat.core.model.*;
import chat.core.util.Stoppable;
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
    private final Stoppable lifetimeManager;
    private final ObjectFactory<TaskExecutor> taskExecutorObjectFactory;

    private long fetchedMessagesStamp;
    private long pendingMessagesStamp;
    private Session session;
    private String authToken;
    private CompletableFuture<String> authRequestFuture;
    private volatile boolean isReading;

    @Autowired
    public DefaultChatClientService(
            ChatClientConsole console,
            @Qualifier("chatClientLifetimeManager") Stoppable lifetimeManager,
            @Qualifier("clientPoolExecutor") ObjectFactory<TaskExecutor> taskExecutorObjectFactory
    ) {

        this.taskExecutorObjectFactory = taskExecutorObjectFactory;
        this.lifetimeManager = lifetimeManager;
        this.console = console;
        this.fetchedMessagesStamp = 0;
        this.pendingMessagesStamp = -1;
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

    private synchronized long getPendingMessagesStamp() {

        return pendingMessagesStamp;
    }

    private synchronized boolean updatePendingMessagesStamp(long messagesStamp) {

        if (this.pendingMessagesStamp >= messagesStamp)
            return false;

        this.pendingMessagesStamp = messagesStamp;
        return true;
    }

    public long getFetchedMessagesStamp() {
        return fetchedMessagesStamp;
    }

    private synchronized boolean updateFetchedMessagesStamp(long fetchedMessagesStamp) {

        if (this.fetchedMessagesStamp >= fetchedMessagesStamp)
            return false;

        this.fetchedMessagesStamp = fetchedMessagesStamp;
        return true;
    }


    private Log getLogger() {

        return logger;
    }

    private void readConsole() {

        try {

            while (!isStopping()) {

                final boolean loginPrompt = StringUtils.isEmpty(getAuthToken());
                final ChatMessage inputMessage = console.readInput(loginPrompt);
                if (inputMessage == null)
                    break;

                sendMessage(inputMessage);
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

    private static boolean isCommandMessage(ChatMessage message) {

        return StringUtils.hasLength(message.getRoute()) ||
                (StringUtils.isEmpty(message.getRoute()) &&
                        StringUtils.hasLength(message.getContent()) &&
                        message.getContent().startsWith("-"));
    }

    private void sendMessage(ChatMessage message) {

        if (message == null) {

            return;
        }

        if (getSession() == null) {

            final ChatMessage errorMessage = new ChatMessage();
            errorMessage.setStandardRoute(ChatStandardRoute.Error);
            errorMessage.setStandardAction(ChatStandardAction.Default);
            errorMessage.setStandardStatus(ChatStandardStatus.Failed);
            errorMessage.setContent(message.getContent());

            receiveMessage(message);
            return;
        }

        if (StringUtils.isEmpty(getAuthToken()) && !isCommandMessage(message)) {

            boolean sendLogin = false;
            final Future<String> authRequestFuture = this.getAuthRequestFuture();
            if (authRequestFuture == null) {

                sendLogin = true;
            }
            else {

                try {

                    final String token = authRequestFuture.get(5, TimeUnit.MINUTES);
                    this.setAuthRequestFuture(null);

                    if (StringUtils.isEmpty(token)) {

                        sendLogin = true;
                    }
                }
                catch (final Exception e) {

                    final ChatMessage errorMessage = ChatMessage.createErrorMessage(e);
                    receiveMessage(errorMessage);
                }
                this.setAuthRequestFuture(null);
            }


            if (sendLogin) {

                message.setStandardRoute(ChatStandardRoute.Auth);
                message.setStandardAction(ChatStandardAction.Login);
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

        if (getSession() != null && StringUtils.hasLength(getAuthToken()) && updatePendingMessagesStamp(stamp)) {

            final ChatMessage message = new ChatMessage();
            message.setStandardRoute(ChatStandardRoute.Message);
            message.setStandardAction(ChatStandardAction.Fetch);
            message.setContent(Long.toString(getFetchedMessagesStamp()));
            sendMessage(message);
        }
    }

    private void receiveMessage(ChatMessage message) {

        if (message.isStandardRoute(ChatStandardRoute.Message)) {

            if (message.isStandardAction(ChatStandardAction.Notify)) {

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
            else if (message.isStandardAction(ChatStandardAction.Fetch)) {

                long stamp = 0;
                try {

                    stamp = Long.parseLong(message.getStatus());
                }
                catch (final NumberFormatException e) {

                    getLogger().error(String.format("Invalid stamp %s%n", message.getStatus()), e);
                }

                if (stamp > 0) {

                    if (updateFetchedMessagesStamp(stamp)) {

                        console.printOutput(message);
                    }
                }
            }
        }
        else if (message.isStandardRoute(ChatStandardRoute.Session)) {

            if (message.isStandardAction(ChatStandardAction.Open)) {

                console.create();
                final ChatMessage welcomeMessage = new ChatMessage();
                welcomeMessage.setStandardRoute(ChatStandardRoute.Help);
                welcomeMessage.setStandardAction(ChatStandardAction.Welcome);
                welcomeMessage.setContent("");
                sendMessage(welcomeMessage);
            }
            else if (message.isStandardAction(ChatStandardAction.Close)) {

                setStopping();
            }
            console.printOutput(message);
        }
        else if (message.isStandardRoute(ChatStandardRoute.Help)) {

            console.printOutput(message);
            if (message.isStandardAction(ChatStandardAction.Welcome)) {
                if (!isReading) {

                    isReading = true;
                    taskExecutorObjectFactory.getObject().execute(this::readConsole);
                }
            }
        }
        else if (message.isStandardRoute(ChatStandardRoute.Auth)) {

            String authToken = getAuthToken();
            final CompletableFuture<String> authRequestFuture = this.getAuthRequestFuture();
            if (message.isStandardAction(ChatStandardAction.Login)) {
                if (message.isStandardStatus(ChatStandardStatus.Success)) {

                    authToken = message.getContent();
                    message.setContent(null);
                    setAuthToken(authToken);

                    updateMessages(getFetchedMessagesStamp());
                }
            }
            else if (message.isStandardAction(ChatStandardAction.Logout)) {

                authToken = null;
                setAuthToken(null);
            }
            console.printOutput(message);

            if (authRequestFuture != null) {

                authRequestFuture.complete(authToken);
            }
        }
        else if (message.isStandardRoute(ChatStandardRoute.Error)) {

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
            message.setStandardRoute(ChatStandardRoute.Session);
            message.setStandardAction(ChatStandardAction.Open);
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
            message.setStandardRoute(ChatStandardRoute.Session);
            message.setStandardAction(ChatStandardAction.Close);
            message.setContent(session.toString());
            receiveMessage(message);

            final ChatClientEnvelope envelope = new ChatClientEnvelope();
            envelope.setAction(ChatAction.CloseSession);

            return new ChatClientResponseIntent(ChatAction.Process, new ChatClientEnvelope[] { envelope });
        }

        return null;
    }

    @Override
    public boolean isAsyncIntent(ChatClientRequestIntent intent) {

        return intent.getAction() == ChatAction.Process;
    }

    @Override
    public void dispose(Session session) {

        setStopped();
    }

    @Override
    public boolean isStopping() {

        return lifetimeManager.isStopping();
    }

    @Override
    public void setStopping() {

        lifetimeManager.setStopping();
        if (console instanceof Stoppable) {

            ((Stoppable) console).setStopping();
        }
    }

    @Override
    public void setStopped() {

        lifetimeManager.setStopped();
    }

    @Override
    public boolean waitStopped(int timeout) {

        return lifetimeManager.waitStopped(timeout);
    }
}
