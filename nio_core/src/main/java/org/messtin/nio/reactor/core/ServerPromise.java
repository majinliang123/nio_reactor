package org.messtin.nio.reactor.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messtin.nio.reactor.core.factory.ThreadFactory;
import org.messtin.nio.reactor.core.worker.AsyncLifeCycle;
import org.messtin.nio.reactor.core.worker.LifeCycle;
import org.messtin.nio.reactor.core.worker.impl.Acceptor;

import java.util.concurrent.TimeUnit;

/**
 * The {@link ServerPromise} is used to control status of server.
 * We use the {@link Acceptor#status} to stand for the status of this class.
 */
public class ServerPromise implements AsyncLifeCycle {
    private static final Logger logger = LogManager.getLogger(ServerPromise.class);

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
        logger.info("Start initialize the server.");
        acceptorThread.start();
    }

    @Override
    public void close() {
        logger.info("Start close the server.");
        acceptor.close();
    }

    @Override
    public void await() throws InterruptedException {
        acceptor.await();
        acceptorThread.join();
        logger.info("Server is closed. Bye..");
    }

    public Status getStatus() {
        return acceptor.getStatus();
    }


}
