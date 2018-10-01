package bill.exp.chat.core.data;

public class StringMessage implements Message {

    private final String content;

    public StringMessage(String content) {
        this.content = content;
    }

    @Override
    public Object getContent() {
        return content;
    }
}
