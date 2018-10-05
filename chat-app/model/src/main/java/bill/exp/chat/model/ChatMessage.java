package bill.exp.chat.model;

@SuppressWarnings("unused")
public class ChatMessage {

    private String type;
    private String title;
    private String text;
    private Long stamp;

    public ChatMessage() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getStamp() {
        return stamp;
    }

    public void setStamp(Long stamp) {
        this.stamp = stamp;
    }

    public static ChatMessage createErrorMessage(String text, String title) {

        ChatMessage result = new ChatMessage();
        result.setText(text);
        result.setTitle(title);
        result.setType("error");

        return result;
    }

    public static ChatMessage createErrorMessage(Exception e) {

        return createErrorMessage(e.getMessage(), e.getClass().getSimpleName());
    }

}
