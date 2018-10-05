package bill.exp.chat.core.data;

@SuppressWarnings("unused")
public enum MessageProcessorBaseOrder {
    FirstValue(100),
    LastValue(900);

    public static final int First = 100;
    public static final int Last = 900;

    private final int value;

    MessageProcessorBaseOrder(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
