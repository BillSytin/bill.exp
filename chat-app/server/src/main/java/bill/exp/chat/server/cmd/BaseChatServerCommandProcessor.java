package bill.exp.chat.server.cmd;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.model.ChatMessage;
import bill.exp.chat.model.ChatStandardAction;
import bill.exp.chat.model.ChatStandardRoute;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.util.StringUtils;

import java.util.Locale;

@SuppressWarnings({"SameReturnValue", "unused"})
public abstract class BaseChatServerCommandProcessor implements ChatServerCommandProcessor {

    @SuppressWarnings("unused")
    @Autowired
    @Qualifier("chatServerMessagesResource")
    private ObjectFactory<AbstractMessageSource> messageSourceObjectFactory;

    protected AbstractMessageSource getMessagesResource() {

        return messageSourceObjectFactory.getObject();
    }

    protected void preprocess(ChatServerCommandProcessingContext context) {
    }

    protected void process(ChatServerCommandProcessingContext context) {
    }

    protected void dispose(Session session) {
    }

    protected abstract String getCommandId();

    protected Locale getContextLocale(ChatServerCommandProcessingContext context) {

        return Locale.US;
    }

    protected String getMessageResource(String id, ChatServerCommandProcessingContext context) {

        return StringUtils.hasLength(id) ? getMessagesResource().getMessage("command." + id, null, getContextLocale(context)) : null;
    }

    protected String getHelpText(ChatServerCommandProcessingContext context) {

        return getMessageResource(getCommandId(), context);
    }

    protected boolean detectDefaultAction(ChatServerCommandProcessingContext context) {

        return context.getProcessingMessage() != null &&
                StringUtils.isEmpty(context.getProcessingMessage().getRoute()) &&
                StringUtils.isEmpty(context.getProcessingMessage().getAction()) &&
                StringUtils.hasLength(context.getProcessingMessage().getContent());
    }

    protected boolean detectProcessingAction(ChatServerCommandProcessingContext context, String action) {

        final ChatMessage message = context.getProcessingMessage();
        if (message == null)
            return false;

        final String commandRoute = getCommandId();
        if (StringUtils.isEmpty(commandRoute))
            return false;

        if (StringUtils.hasLength(message.getRoute()) && !message.getRoute().equals(commandRoute)) {

            return false;
        }

        if (StringUtils.isEmpty(action)) {

            return StringUtils.hasLength(message.getRoute()) && StringUtils.isEmpty(message.getAction());
        }

        if (StringUtils.hasLength(message.getAction())) {

            return message.getAction().equals(action);
        }

        if (StringUtils.hasLength(message.getContent())) {

            if (message.getContent().equals("-" + action)) {

                message.setRoute(commandRoute);
                message.setAction(action);
                message.setContent(null);
                return true;
            }
            else if (message.getContent().startsWith("-" + action + " ")) {

                message.setRoute(commandRoute);
                message.setAction(action);
                message.setContent(message.getContent().substring(action.length() + 2));
                return true;
            }
        }

        return false;
    }

    @Override
    public void process(ChatServerCommandProcessingPhase phase, ChatServerCommandProcessingContext context) {

        switch (phase) {

            case Preprocess:
                preprocess(context);
                break;

            case Process:
                process(context);
                break;

            case Help:

                String helpText = getHelpText(context);
                if (StringUtils.hasLength(helpText)) {

                    final ChatMessage message = new ChatMessage();
                    message.setStandardRoute(ChatStandardRoute.Help);
                    message.setStandardAction(ChatStandardAction.Help);
                    message.setStatus(getCommandId());
                    message.setContent(helpText);
                    context.getOutput().getMessages().add(message);
                }
                break;

            case Dispose:
                dispose(context.getSession());
                break;
        }

    }
}
