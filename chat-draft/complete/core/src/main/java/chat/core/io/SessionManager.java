package chat.core.io;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface SessionManager {

    long generateSessionId();
    void addSession(Session session);
    void removeSession(Session session);
    void foreachSession(Consumer<Session> action);
}
