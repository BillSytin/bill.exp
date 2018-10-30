package bill.exp.chat.server.users;

import bill.exp.chat.core.model.ChatUser;

public interface ChatServerUser {

    String getName();
    boolean isAuthenticated();
    boolean isLoggedIn();

    default ChatUser toModel() {

        final ChatUser result = new ChatUser();
        result.setName(getName());
        return result;
    }
}
