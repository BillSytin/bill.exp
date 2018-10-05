package bill.exp.chat.server.users;

public class SimpleChatServerUser implements ChatServerUser {

    private final String name;
    private boolean isAuthenticated;

    public SimpleChatServerUser(String name, boolean isAuthenticated) {

        this.name = name;
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public boolean isAuthenticated() {

        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }
}
