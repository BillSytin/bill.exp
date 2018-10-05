package bill.exp.chat.core.data;

import bill.exp.chat.core.api.RequestIntent;

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
