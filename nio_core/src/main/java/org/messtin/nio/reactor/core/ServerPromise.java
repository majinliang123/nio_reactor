package org.messtin.nio.reactor.core;

import org.messtin.nio.reactor.core.factory.ThreadFactory;
import org.messtin.nio.reactor.core.worker.LifeCycle;
import org.messtin.nio.reactor.core.worker.impl.Acceptor;

import java.util.concurrent.TimeUnit;

/**
 * The status of server. We use the {@link Acceptor#status} to stand for the status of this class.
 */
public class ServerPromise implements LifeCycle {

    private Thread acceptorThread;
    private Acceptor acceptor;

    public ServerPromise(Acceptor acceptor, ThreadFactory factory) {
        this.acceptor = acceptor;
        acceptorThread = factory.create(acceptor);
    }

    public ServerPromise start() {
        initialize();
        return this;
    }

    @Override
    public void initialize() {
        acceptorThread.start();
    }

    @Override
    public void close() {
        acceptor.close();
    }

    @Override
    public void await() throws InterruptedException {
        acceptorThread.join();
    }

    @Override
    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        acceptorThread.join(unit.toMillis(timeout));
    }

    public Status getStatus() {
        return acceptor.getStatus();
    }


}
