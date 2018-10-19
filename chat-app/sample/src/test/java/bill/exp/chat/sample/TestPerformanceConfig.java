package bill.exp.chat.sample;

import bill.exp.chat.client.api.ChatClientService;
import bill.exp.chat.server.api.ChatServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@SuppressWarnings("unused")
@TestConfiguration
@Profile("performance")
class TestPerformanceConfig {

    @Autowired
    private TestPerformanceClientServer clientServer;

    public TestPerformanceConfig() {

    }

    @Bean
    @Primary
    ChatServerService getServer() {

        return clientServer.getServer();
    }

    @Bean
    @Primary
    ChatClientService getClient() {

        return clientServer.getClient();
    }

}
