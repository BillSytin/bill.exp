package chat.core.data;

class StringMessage implements Message {

    private final String[] content;

    public StringMessage(String[] content) {
        this.content = content;
    }

    public String[] getStrings() {

        return content;
    }
}
