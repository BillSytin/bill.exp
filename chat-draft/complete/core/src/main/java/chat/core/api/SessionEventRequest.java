package chat.core.api;

import chat.core.data.SessionEvent;

@SuppressWarnings("unused")
public class SessionEventRequest implements Request {

    private final long id;
    private final SessionEvent event;

    public SessionEventRequest(long id, SessionEvent event) {

        this.id = id;
        this.event = event;
    }

    public long getId() {

        return id;
    }

    public SessionEvent getEvent() {

        return event;
    }

    @Override
    public String[] getContent() {

        return null;
    }
}
