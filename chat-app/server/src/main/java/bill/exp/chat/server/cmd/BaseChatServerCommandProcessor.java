package bill.exp.chat.server.cmd;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.model.ChatMessage;
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

    protected String getHelpText() {

        return StringUtils.hasLength(getCommandId()) ? getMessagesResource().getMessage(getCommandId(), null, Locale.US) : null;
    }

    protected boolean detectProcessingCommmand(ChatServerCommandProcessingContext context) {

        return detectProcessingCommmand(context, getCommandId());
    }

    protected boolean detectProcessingCommmand(ChatServerCommandProcessingContext context, String commandId) {

        return StringUtils.hasLength(commandId) &&
                context.getProcessingMessage() != null &&
                commandId.equals(parseMessageType(context.getProcessingMessage(), commandId));
    }

    protected static String parseMessageType(ChatMessage message, String supposedCommandId) {

        if (message != null && StringUtils.isEmpty(message.getType())) {

            if (StringUtils.hasLength(supposedCommandId) && StringUtils.hasLength(message.getText())) {

                if (message.getText().equals("-" + supposedCommandId)) {

                    message.setType(supposedCommandId);
                    message.setText(null);
                }
                else if (message.getText().startsWith("-" + supposedCommandId + " ")) {

                    message.setType(supposedCommandId);
                    message.setText(message.getText().substring(supposedCommandId.length() + 2));
                }
            }
        }

        return message != null ? message.getType() : null;
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

                String helpText = getHelpText();
                if (StringUtils.hasLength(helpText)) {

                    final ChatMessage message = new ChatMessage();
                    message.setType("help");
                    message.setTitle(getCommandId());
                    message.setText(helpText);
                    context.getOutput().getMessages().add(message);
                }
                break;

            case Dispose:
                dispose(context.getSession());
                break;
        }

    }
}
