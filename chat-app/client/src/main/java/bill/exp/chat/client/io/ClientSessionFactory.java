package bill.exp.chat.client.io;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.io.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Qualifier("clientSessionFactory")
public class ClientSessionFactory implements SessionFactory {
    private final ApplicationContext context;

    @Autowired
    public ClientSessionFactory(
            ApplicationContext context
    ) {
        this.context = context;
    }

    @Override
    public Session createSession() {

        return context.getBean(ClientSession.class);
    }
}
