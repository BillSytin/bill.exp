package chat.core.model;

public enum ChatStandardRoute {
    Session("session"),
    Auth("auth"),
    Help("help"),
    Error("error"),
    Message("message");

    private final String value;

    ChatStandardRoute(String value) {

        this.value = value;
    }

    @Override
    public String toString() {

        return this.value;
    }
}
