package chat.core.data;

import chat.core.api.ResponseIntent;

@SuppressWarnings("unused")
public class ResponseIntentMessage implements Message {

    private final ResponseIntent intent;

    public ResponseIntentMessage(ResponseIntent intent) {

        this.intent = intent;
    }

    public ResponseIntent getIntent() {

        return intent;
    }
}
