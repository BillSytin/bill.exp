package chat.server;

import chat.core.util.Stoppable;
import chat.server.msg.ChatServerMessageNotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Scanner;

@SuppressWarnings("unused")
@Component
@Profile("!test")
public class ChatServerCommandLineRunner implements CommandLineRunner {
    private final TaskExecutor executor;
    private final Runnable worker;
    private final Stoppable lifeTimeManager;
    private final ChatServerMessageNotificationsService notificationsService;
    private final ResourceBundleMessageSource messageSource;

    @Autowired
    public ChatServerCommandLineRunner(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("inplaceExecutor") TaskExecutor executor,
            @Qualifier("mainWorker") Runnable worker,
            @Qualifier("chatServerMessagesResource") ResourceBundleMessageSource messageSource,
            ChatServerMessageNotificationsService notificationsService
    ) {

        this.lifeTimeManager = lifeTimeManager;
        this.executor = executor;
        this.worker = worker;
        this.messageSource = messageSource;
        this.notificationsService = notificationsService;
    }

    @SuppressWarnings("SameReturnValue")
    private Locale getLocale() {

        return Locale.US;
    }

    @Override
    public void run(String... args) {

        System.out.println(messageSource.getMessage("server.starting", new Object[] { worker.toString() }, getLocale()));
        System.out.println(messageSource.getMessage("server.help", null, getLocale()));
        System.out.println();

        executor.execute(worker);

        final Scanner scanner = new Scanner(System.in);
        while (!lifeTimeManager.isStopping()) {

            final String input = scanner.nextLine().trim();

            if ("q".equals(input) || "quit".equals(input))
                break;
            else
                System.out.println(messageSource.getMessage("server.unknown-command", new String[] { input }, getLocale()));
        }

        System.out.println(messageSource.getMessage("server.exit", null, getLocale()));
        lifeTimeManager.setStopping();

        if (worker instanceof Stoppable)
            ((Stoppable) worker).setStopping();

        if (notificationsService instanceof Stoppable)
            ((Stoppable) notificationsService).setStopping();

        lifeTimeManager.waitStopped(10000);
        System.exit(0);
    }
}
