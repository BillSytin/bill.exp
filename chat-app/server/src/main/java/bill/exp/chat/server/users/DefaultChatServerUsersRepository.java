package bill.exp.chat.server.users;

import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
@Repository
public class DefaultChatServerUsersRepository implements ChatServerUsersRepository {

    private final Map<String, String> usersByName;
    private final Map<String, ChatServerUser> usersByToken;

    public DefaultChatServerUsersRepository() {

        usersByName = new HashMap<>();
        usersByToken = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized ChatServerUserToken login(String scope, ChatServerUser user) throws IllegalArgumentException, IllegalStateException {

        final String userName = user.getName();
        if (usersByName.get(userName) != null) {

            throw new IllegalArgumentException();
        }

        final ChatServerUserToken userToken = new ChatServerSimpleUserToken(
                String.format("%s:%s", scope, UUID.randomUUID().toString()));
        if (usersByToken.get(userToken.toString()) != null) {

            throw new IllegalStateException();
        }

        usersByName.put(userName, userToken.toString());
        usersByToken.put(userToken.toString(), user);

        return userToken;
    }

    @Override
    public ChatServerUser getUserByToken(ChatServerUserToken token) {

        return usersByToken.get(token.toString());
    }

    @Override
    public synchronized void logout(ChatServerUserToken token) {

        final String userToken = token.toString();
        final ChatServerUser user = usersByToken.get(userToken);
        if (user != null) {

            usersByToken.remove(userToken);
            usersByName.remove(user.getName());
        }
    }
}
