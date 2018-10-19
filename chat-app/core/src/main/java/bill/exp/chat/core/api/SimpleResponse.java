package bill.exp.chat.core.api;

public class SimpleResponse implements Response {

    private final String[] content;

    public SimpleResponse(String[] content) {

        this.content = content;
    }

    @Override
    public String[] getContent() {

        return content;
    }
}
