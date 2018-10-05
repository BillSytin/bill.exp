package bill.exp.chat.server.msg;

public interface ChatServerMessageNotificationsService {

    void fireNewMessageNotification(long messageStamp);
}
