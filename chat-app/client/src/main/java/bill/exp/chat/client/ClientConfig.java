package bill.exp.chat.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ClientConfig {

    @Bean("serverAddress")
    public InetSocketAddress serverAddress() {
        return new InetSocketAddress("localhost", 7777);
    }
}
