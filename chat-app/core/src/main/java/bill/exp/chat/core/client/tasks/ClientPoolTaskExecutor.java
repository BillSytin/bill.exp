package bill.exp.chat.core.client.tasks;

import bill.exp.chat.core.tasks.BasePoolTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("clientPoolExecutor")
public class ClientPoolTaskExecutor extends BasePoolTaskExecutor {

    @Autowired
    public ClientPoolTaskExecutor(ClientPoolConfig config) {

        super("cli", config.getPoolSize());
    }
}
