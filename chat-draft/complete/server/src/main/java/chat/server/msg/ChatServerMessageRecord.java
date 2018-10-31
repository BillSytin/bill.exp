package chat.server.msg;

import chat.core.model.ChatMessage;
import chat.core.model.ChatUser;

@SuppressWarnings("unused")
public class ChatServerMessageRecord {

    private ChatMessage message;
    private ChatUser user;
    private long stamp;
    private long sessionId;

    public ChatServerMessageRecord() {

    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public ChatUser getUser() {
        return user;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }
}
