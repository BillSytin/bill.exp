package bill.exp.chat.core.server.io;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.io.SessionFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("serverSessionFactory")
public class ServerSessionFactory implements SessionFactory {

    private final ObjectFactory<ServerSession> factory;

    @Autowired
    public ServerSessionFactory(
            ObjectFactory<ServerSession> factory
    ) {
        this.factory = factory;
    }

    @Override
    public Session createSession() {

        return factory.getObject();
    }
}
