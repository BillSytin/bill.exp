package bill.exp.chat.core.model;

import java.util.Date;

@SuppressWarnings("unused")
public class ChatBaseEnvelope {

    private Date timestamp;
    private ChatMessageList messages;
    private ChatAction action;
    private String authToken;

    public ChatBaseEnvelope() {

        timestamp = new Date();
        action = null;
    }

    public Date getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(Date value) {

        timestamp = value;
    }

    public ChatMessageList getMessages() {

        return messages;
    }

    public void setMessages(ChatMessageList messages) {

        this.messages = messages;
    }

    public ChatAction getAction() {

        return action;
    }

    public void setAction(ChatAction action) {

        this.action = action;
    }

    public String getAuthToken() {

        return authToken;
    }

    public void setAuthToken(String authToken) {

        this.authToken = authToken;
    }
}
