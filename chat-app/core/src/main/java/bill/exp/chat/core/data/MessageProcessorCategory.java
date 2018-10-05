package bill.exp.chat.core.data;

@SuppressWarnings("unused")
public enum MessageProcessorCategory {
    InputBufferValue(-2000),
    InputConvertValue(-1000),
    ProcessValue(0),
    OutputConvertValue(1000),
    OutputBufferValue(2000);

    public static final int InputBuffer = -2000;
    public static final int InputConvert = -1000;
    public static final int Process = 0;
    public static final int OutputConvert = 1000;
    public static final int OutputBuffer = 2000;

    private final int value;

    MessageProcessorCategory(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
