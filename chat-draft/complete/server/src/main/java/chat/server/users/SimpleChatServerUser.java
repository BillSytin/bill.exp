package chat.server.users;

public class SimpleChatServerUser implements ChatServerUser {

    private final String name;
    private boolean isAuthenticated;
    private final boolean isLoggedIn;

    public SimpleChatServerUser(String name, boolean isAuthenticated, boolean isLoggedIn) {

        this.name = name;
        this.isAuthenticated = isAuthenticated;
        this.isLoggedIn = isLoggedIn;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public boolean isLoggedIn() {

        return isLoggedIn || isAuthenticated;
    }

    @Override
    public boolean isAuthenticated() {

        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }
}
