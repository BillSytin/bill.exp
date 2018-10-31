package chat.core.api;

public class SimpleRequest implements Request {

    private final String[] content;

    public SimpleRequest(String[] content) {

        this.content = content;
    }

    @Override
    public String[] getContent() {

        return content;
    }
}
