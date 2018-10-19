package bill.exp.chat.client;

import bill.exp.chat.core.client.io.ClientChannel;
import bill.exp.chat.core.util.Stoppable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Component
@Profile({"client"})
public class ChatClientCommandLineRunner implements CommandLineRunner {

    private final ClientChannel clientChannel;
    private final Stoppable lifeTimeManager;

    @Autowired
    public ChatClientCommandLineRunner(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("tcpConnectChannel") ClientChannel clientChannel
    ) {

        this.lifeTimeManager = lifeTimeManager;
        this.clientChannel = clientChannel;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println(String.format("Connecting to %s...", clientChannel.toString()));

        clientChannel.connect().get();
    }
}
