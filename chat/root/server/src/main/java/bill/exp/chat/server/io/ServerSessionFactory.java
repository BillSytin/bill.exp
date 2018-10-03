package bill.exp.chat.server.io;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.io.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component("serverSessionFactory")
public class ServerSessionFactory implements SessionFactory {

    private final ApplicationContext context;

    @Autowired
    public ServerSessionFactory(
            ApplicationContext context
    ) {
        this.context = context;
    }

    @Override
    public Session createSession() {

        return context.getBean(ServerSession.class);
    }
}
