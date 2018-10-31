package chat.server.msg;

import chat.core.api.RequestIntent;
import chat.core.data.Message;
import chat.core.data.RequestIntentMessage;
import chat.core.io.SessionManager;
import chat.core.model.*;
import chat.core.util.Stoppable;
import chat.server.api.ChatServerRequestIntent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unused")
@Component
public class DefaultChatServerMessageNotificationsService implements ChatServerMessageNotificationsService, Stoppable, DisposableBean {

    private static final int MessageStampNullValue = -1;
    private final int notificationTimeout;
    private final SessionManager sessionManager;
    private final TaskExecutor taskExecutor;
    private final AtomicLong notificationMessageStamp;
    private final Timer timer;

    @Autowired
    public DefaultChatServerMessageNotificationsService(
            ChatServerMessagesRepositoryConfig config,
            @Qualifier("serverSessionManager") SessionManager sessionManager,
            @Qualifier("serverPoolHandlerExecutor") TaskExecutor taskExecutor
    ) {

        this.notificationTimeout = config.getNotificationTimeout();
        this.sessionManager = sessionManager;
        this.taskExecutor = taskExecutor;
        this.notificationMessageStamp = new AtomicLong(MessageStampNullValue);
        timer = new Timer();
    }

    @Override
    public void fireNewMessageNotification(long messageStamp) {

        if (notificationMessageStamp.getAndSet(messageStamp) == MessageStampNullValue) {

            scheduleNotification();
        }
    }

    private void scheduleNotification() {

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {

                        taskExecutor.execute(() -> runNotification());
                    }
                },
                this.notificationTimeout
        );
    }

    private void runNotification() {

        final long stamp = notificationMessageStamp.getAndSet(MessageStampNullValue);
        if (stamp != MessageStampNullValue) {

            notifySessions(stamp);
        }
    }

    private void notifySessions(long stamp) {

        final ChatMessage notifyMessage = new ChatMessage();
        notifyMessage.setContent(Long.toString(stamp));
        notifyMessage.setStandardRoute(ChatStandardRoute.Message);
        notifyMessage.setStandardAction(ChatStandardAction.Notify);

        final ChatClientEnvelope content = new ChatClientEnvelope();
        content.setMessages(new ChatMessageList());
        content.getMessages().add(notifyMessage);
        final RequestIntent requestIntent = new ChatServerRequestIntent(
                ChatAction.Process,
                new ChatClientEnvelope[] { content });
        final Message intentMessage = new RequestIntentMessage(requestIntent);

        sessionManager.foreachSession(session -> session.submit(intentMessage));
    }

    @Override
    public void destroy() {

        timer.cancel();
    }

    @Override
    public boolean isStopping() {

        return false;
    }

    @Override
    public void setStopping() {

        timer.cancel();
    }

    @Override
    public void setStopped() {

    }

    @Override
    public boolean waitStopped(int timeout) {

        return true;
    }
}
