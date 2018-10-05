package bill.exp.chat.server.msg;

import bill.exp.chat.core.api.ResponseIntent;
import bill.exp.chat.core.data.Message;
import bill.exp.chat.core.data.ResponseIntentMessage;
import bill.exp.chat.core.io.SessionManager;
import bill.exp.chat.model.ChatBaseAction;
import bill.exp.chat.model.ChatMessage;
import bill.exp.chat.model.ChatMessageList;
import bill.exp.chat.model.ChatServerEnvelope;
import bill.exp.chat.server.api.ChatServerResponseIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unused")
@Service
public class DefaultChatServerMessageNotificationsService implements ChatServerMessageNotificationsService {

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
        notifyMessage.setText(Long.toString(stamp));
        notifyMessage.setType("notify");
        notifyMessage.setTitle("newmessages");

        final ChatServerEnvelope content = new ChatServerEnvelope();
        content.setMessages(new ChatMessageList());
        content.getMessages().add(notifyMessage);
        final ResponseIntent responseIntent = new ChatServerResponseIntent(ChatBaseAction.Process, content);
        final Message intentMessage = new ResponseIntentMessage(responseIntent);

        sessionManager.foreachSession(session -> session.submit(intentMessage));
    }
}
