package chat.core.data;

import chat.core.api.RequestIntent;

@SuppressWarnings("unused")
public class RequestIntentMessage implements Message {

    private final RequestIntent intent;

    public RequestIntentMessage(RequestIntent intent) {
        this.intent = intent;
    }

    public RequestIntent getIntent() {
        return intent;
    }
}
