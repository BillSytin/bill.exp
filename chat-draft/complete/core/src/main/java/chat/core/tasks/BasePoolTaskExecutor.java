package chat.core.tasks;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class BasePoolTaskExecutor extends ThreadPoolTaskExecutor implements ExecutorService {

    protected BasePoolTaskExecutor(String prefix, int poolSize) {

        setThreadNamePrefix(prefix);
        setCorePoolSize(poolSize);
        initialize();
    }

    @Override
    public List<Runnable> shutdownNow() {

        ExecutorService executor = getThreadPoolExecutor();
        return executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {

        ExecutorService executor = getThreadPoolExecutor();
        return executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {

        ExecutorService executor = getThreadPoolExecutor();
        return executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {

        ExecutorService executor = getThreadPoolExecutor();
        return executor.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {

        ExecutorService executor = getThreadPoolExecutor();
        return executor.submit(task, result);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {

        ExecutorService executor = getThreadPoolExecutor();
        return executor.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {

        ExecutorService executor = getThreadPoolExecutor();
        return executor.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {

        ExecutorService executor = getThreadPoolExecutor();
        return executor.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        ExecutorService executor = getThreadPoolExecutor();
        return executor.invokeAny(tasks, timeout, unit);
    }
}
