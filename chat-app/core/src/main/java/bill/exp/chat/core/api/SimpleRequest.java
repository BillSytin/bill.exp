package bill.exp.chat.core.api;

public class SimpleRequest implements Request {

    private final String action;
    private final String content;

    public SimpleRequest(String action, String content) {

        this.action = action;
        this.content = content;
    }

    @Override
    public String getContent() {

        return content;
    }

    @Override
    public String getAction() {

        return action;
    }
}
