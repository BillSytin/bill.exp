package chat.core.client.io;

import chat.core.io.Session;
import chat.core.io.SessionFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Qualifier("clientSessionFactory")
public class ClientSessionFactory implements SessionFactory {
    private final ObjectFactory<ClientSession> factory;

    @Autowired
    public ClientSessionFactory(
            ObjectFactory<ClientSession> factory
    ) {
        this.factory = factory;
    }

    @Override
    public Session createSession() {

        return factory.getObject();
    }
}
