package bill.exp.chat.server.users;

@SuppressWarnings("unused")
public interface ChatServerUsersRepository {

    ChatServerUserToken login(String scope, ChatServerUser user) throws IllegalArgumentException, IllegalStateException;
    ChatServerUser getUserByToken(ChatServerUserToken token);
    void logout(ChatServerUserToken token);
}
