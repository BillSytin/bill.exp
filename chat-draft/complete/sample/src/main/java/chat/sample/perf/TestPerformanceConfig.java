package chat.sample.perf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "test.performance")
public class TestPerformanceConfig {

    @Min(2)
    @Max(2000)
    private int clientCount;

    @Min(2)
    @Max(2000)
    private int clientSleepTime;

    @Min(10)
    @Max(600)
    private int testTimeSec;

    public TestPerformanceConfig() {

        clientCount = 1000;
        clientSleepTime = 0;
        testTimeSec = 60;
    }

    public int getClientCount() {
        return clientCount;
    }

    public void setClientCount(int clientCount) {
        this.clientCount = clientCount;
    }

    public int getClientSleepTime() {
        return clientSleepTime;
    }

    public void setClientSleepTime(int clientSleepTime) {
        this.clientSleepTime = clientSleepTime;
    }

    public int getTestTimeSec() {
        return testTimeSec;
    }

    public void setTestTimeSec(int testTimeSec) {
        this.testTimeSec = testTimeSec;
    }
}
