package pt.up.fe.cpd.g13.common.service;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Service implements Runnable {

    protected final Logger logger;
    private volatile boolean shouldTick = true;
    private volatile Thread currentThread = null;

    protected Service(Class<? extends Service> serviceClass) {
        this.logger = Logger.getLogger(serviceClass.getName());
    }

    @Override
    public final void run() {
        currentThread = Thread.currentThread();

        try {
            runWithResources(() -> {
                try {
                    while (true) {

                        synchronized (this) {
                            if (!shouldTick)
                                break;
                        }

                        var shouldContinue = tick();
                        if (!shouldContinue)
                            break;

                        synchronized (this) {
                            shouldTick = shouldTick && !Thread.currentThread().isInterrupted();
                        }

                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "An exception was thrown while this service was ticking", e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, "An exception was thrown while this service was starting or closing", e);
            e.printStackTrace();
        }

        currentThread = null;

        synchronized (this) {
            notifyAll();
        }
    }

    public boolean isRunning() {
        return currentThread != null;
    }

    public synchronized void stopNow() {
        shouldTick = false;
        if (currentThread != null)
            currentThread.interrupt();
    }

    public synchronized void stop() throws InterruptedException {
        this.stopNow();
        while (isRunning()) wait();
    }

    protected void runWithResources(Runnable service) throws Exception {
        service.run();
    }

    protected abstract boolean tick() throws Exception;
}
