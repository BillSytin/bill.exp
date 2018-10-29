package bill.exp.chat.server;

import bill.exp.chat.core.util.Stoppable;
import bill.exp.chat.server.msg.ChatServerMessageNotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@SuppressWarnings("unused")
@Component
@Profile("!test")
public class ChatServerCommandLineRunner implements CommandLineRunner {
    private final TaskExecutor executor;
    private final Runnable worker;
    private final Stoppable lifeTimeManager;
    private final ChatServerMessageNotificationsService notificationsService;

    @Autowired
    public ChatServerCommandLineRunner(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("inplaceExecutor") TaskExecutor executor,
            @Qualifier("mainWorker") Runnable worker,
            ChatServerMessageNotificationsService notificationsService
    ) {

        this.lifeTimeManager = lifeTimeManager;
        this.executor = executor;
        this.worker = worker;
        this.notificationsService = notificationsService;
    }

    @Override
    public void run(String... args) {

        System.out.println("Starting chat server");
        System.out.println("\t- Type 'q' and press enter to quit the application");
        System.out.println();

        executor.execute(worker);

        final Scanner scanner = new Scanner(System.in);
        while (!lifeTimeManager.isStopping()) {

            final String input = scanner.nextLine().trim();

            if ("q".equals(input) || "quit".equals(input))
                break;
            else
                System.out.println(String.format("Unknown command: %s", input));
        }

        System.out.println("Exiting application...");
        lifeTimeManager.setStopping();

        if (worker instanceof Stoppable)
            ((Stoppable) worker).setStopping();

        if (notificationsService instanceof Stoppable)
            ((Stoppable) notificationsService).setStopping();

        lifeTimeManager.waitStopped(10000);
        System.exit(0);
    }
}
