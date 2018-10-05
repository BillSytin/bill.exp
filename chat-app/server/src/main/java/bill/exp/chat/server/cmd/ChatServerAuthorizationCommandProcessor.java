package bill.exp.chat.server.cmd;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.model.ChatBaseAction;
import bill.exp.chat.model.ChatMessage;
import bill.exp.chat.server.users.*;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@SuppressWarnings("unused")
@Component
@Order(-100)
@Scope("prototype")
public class ChatServerAuthorizationCommandProcessor extends BaseChatServerCommandProcessor {

    private final ObjectFactory<ChatServerUsersRepository> usersRepositoryObjectFactory;
    private ChatServerUserToken currentUserToken;

    @Autowired
    public ChatServerAuthorizationCommandProcessor(ObjectFactory<ChatServerUsersRepository> usersRepositoryObjectFactory) {

        this.usersRepositoryObjectFactory = usersRepositoryObjectFactory;
    }

    private ChatServerUsersRepository getUsers() {

        return usersRepositoryObjectFactory.getObject();
    }

    @Override
    protected String getCommandId() {

        return "auth";
    }

    @Override
    protected void dispose(Session session) {

        logout();
    }

    private synchronized ChatServerUserToken releaseCurrentToken() {

        ChatServerUserToken userToken = currentUserToken;
        currentUserToken = null;
        return userToken;
    }

    private void logout() {

        ChatServerUserToken userToken = releaseCurrentToken();
        if (userToken != null) {

            getUsers().logout(userToken);
        }
    }

    private synchronized void login(ChatServerUserToken userToken) {

        logout();
        currentUserToken = userToken;
    }

    private synchronized boolean verifyToken(ChatServerUserToken inputToken) {

        return inputToken.equals(currentUserToken);
    }

    @Override
    protected void preprocess(ChatServerCommandProcessingContext context) {

        if (context.getIntentAction() == ChatBaseAction.CloseSession) {

            logout();
        }

        if (context.getInput() != null && StringUtils.hasLength(context.getInput().getAuthToken())) {

            boolean isAuthenticated = false;
            final ChatServerUserToken inputUserToken = new ChatServerSimpleUserToken(context.getInput().getAuthToken());
            if (verifyToken(inputUserToken)) {

                final ChatServerUser user = getUsers().getUserByToken(inputUserToken);
                if (user != null) {

                    context.setUser(user);
                    if (user.isAuthenticated())
                        isAuthenticated = true;
                }
            }

            if (!isAuthenticated) {

                context.getOutput().getMessages().add(ChatMessage.createErrorMessage("Invalid auth token", getCommandId()));
                context.setCompleted();
            }
        }
    }

    @Override
    protected void process(ChatServerCommandProcessingContext context) {

        if (detectProcessingCommmand(context, "login")) {

            final String userName = context.getProcessingMessage().getText();
            final SimpleChatServerUser user = new SimpleChatServerUser(userName, false);

            ChatServerUserToken userToken = null;
            try {

                userToken = getUsers().login(Long.toString(context.getSession().getId()), user);

            } catch (final IllegalArgumentException e) {

                final ChatMessage errorMessage = ChatMessage.createErrorMessage(String.format("User '%s' already exists", userName), "login");
                context.getOutput().getMessages().add(errorMessage);
                context.setCompleted();

            } catch (final Exception e) {

                final ChatMessage errorMessage = ChatMessage.createErrorMessage(e);
                context.getOutput().getMessages().add(errorMessage);
                context.setCompleted();
            }

            if (userToken != null) {

                login(userToken);
                user.setAuthenticated(true);
                context.setUser(user);
                context.getOutput().setAuthToken(userToken.toString());
            }
        }

        if (detectProcessingCommmand(context, "logout")) {

            logout();
            context.setUser(null);
        }
    }
}
