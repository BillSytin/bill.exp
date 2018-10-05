package bill.exp.chat.server.users;

import bill.exp.chat.model.ChatUser;

public interface ChatServerUser {

    String getName();
    boolean isAuthenticated();

    default ChatUser toModel() {

        final ChatUser result = new ChatUser();
        result.setName(getName());
        return  result;
    }
}
