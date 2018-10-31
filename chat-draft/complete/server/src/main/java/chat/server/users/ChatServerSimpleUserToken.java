package chat.server.users;

@SuppressWarnings("unused")
public class ChatServerSimpleUserToken implements ChatServerUserToken {

    private final String value;

    public ChatServerSimpleUserToken(String value) {

        this.value = value;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof ChatServerUserToken && toString().equals(o.toString());
    }

    @Override
    public String toString() {

        return value;
    }
}
