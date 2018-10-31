package chat.core.data;

public class SessionEventMessage implements Message {

    private final long id;

    private final SessionEvent event;

    public SessionEventMessage(long id, SessionEvent event) {

        this.id = id;
        this.event = event;
    }

    public long getId() {

        return id;
    }

    public SessionEvent getEvent() {

        return event;
    }
}
