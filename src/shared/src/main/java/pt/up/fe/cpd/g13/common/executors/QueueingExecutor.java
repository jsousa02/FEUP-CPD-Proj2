package pt.up.fe.cpd.g13.common.executors;

import java.util.LinkedList;
import java.util.concurrent.Executor;

public class QueueingExecutor implements Executor {

    private LinkedList<Runnable> pendingCommands = new LinkedList<>();

    @Override
    public synchronized void execute(Runnable command) {
        pendingCommands.add(command);
        notify();
    }

    public void flushNow() {
        LinkedList<Runnable> commandsToBeExecuted;
        synchronized (this) {
            commandsToBeExecuted = pendingCommands;
            pendingCommands = new LinkedList<>();
        }

        commandsToBeExecuted.forEach(Runnable::run);
    }

    public void flush() throws InterruptedException {
        synchronized (this) {
            while (pendingCommands.isEmpty())
                wait();
        }

        flushNow();
    }

    public void flush(long timeoutMillis) throws InterruptedException {
        var start = System.currentTimeMillis();
        var end = start + timeoutMillis;

        synchronized (this) {
            while (pendingCommands.isEmpty()) {
                var millisLeft = end - System.currentTimeMillis();
                if (millisLeft <= 0)
                    return;

                wait(millisLeft);
            }
        }

        flushNow();
    }
}
