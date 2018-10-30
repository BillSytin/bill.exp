package bill.exp.chat.core.model;

public enum ChatStandardStatus {
    Success("success"),
    Failed("failed");

    private final String value;

    ChatStandardStatus(String value) {

        this.value = value;
    }

    @Override
    public String toString() {

        return this.value;
    }
}
