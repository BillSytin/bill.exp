package bill.exp.chat.model;

@SuppressWarnings("unused")
public class ChatMessage {

    private String route;
    private String action;
    private String status;
    private String content;
    private Long stamp;
    private ChatUser author;

    public ChatMessage() {

    }

    @Override
    public String toString() {

        return String.format("%s %s: %s %s", getRoute(), getAction(), getStatus(), getContent());
    }

    public String getRoute() { return route; }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getAction() { return action; }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) { this.content = content; }

    public Long getStamp() {
        return stamp;
    }

    public void setStamp(Long stamp) {
        this.stamp = stamp;
    }

    public ChatUser getAuthor() {
        return author;
    }

    public void setAuthor(ChatUser author) {
        this.author = author;
    }

    public static ChatMessage createErrorMessage(String content, String action) {

        final ChatMessage result = new ChatMessage();
        result.setContent(content);
        result.setAction(action);
        result.setRoute(ChatStandardRoute.Error.toString());

        return result;
    }

    public static ChatMessage createErrorMessage(Exception e) {

        final ChatMessage result = createErrorMessage(e.getMessage(), ChatStandardAction.Default.toString());
        result.setStatus(e.getClass().getSimpleName());
        return result;
    }

}
