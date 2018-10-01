package bill.exp.chat.server;

import bill.exp.chat.core.util.Stoppable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@Profile("!test")
public class CommandLineConsole implements CommandLineRunner {
    @Autowired
    @Qualifier("inplaceExecutor")
    private TaskExecutor executor;

    @Autowired
    @Qualifier("mainWorker")
    private Runnable worker;

    @Autowired
    @Qualifier("mainLifetimeManager")
    private Stoppable lifeTimeManager;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Please enter some command and press <enter>: ");
        System.out.println("\tNote:");
        System.out.println("\t- Entering q will quit the application");
        System.out.print("\n");

        executor.execute(worker);

        final Scanner scanner = new Scanner(System.in);
        while (!lifeTimeManager.getIsStopping()) {

            final String input = scanner.nextLine().trim();

            if ("q".equals(input) || "quit".equals(input)) {
                break;
            }
        }

        System.out.println("Exiting application...");
        lifeTimeManager.setIsStopping();

        if (worker instanceof DisposableBean)
            ((DisposableBean) worker).destroy();

        lifeTimeManager.waitStopped(10000);
    }
}
