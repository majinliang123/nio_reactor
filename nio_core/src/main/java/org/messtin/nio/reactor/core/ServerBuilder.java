package org.messtin.nio.reactor.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messtin.nio.reactor.core.factory.EventHandlerFactory;
import org.messtin.nio.reactor.core.factory.impl.AcceptorThreadFactory;
import org.messtin.nio.reactor.core.util.Preconditions;
import org.messtin.nio.reactor.core.worker.Dispatcher;
import org.messtin.nio.reactor.core.worker.LifeCycle;
import org.messtin.nio.reactor.core.worker.impl.Acceptor;
import org.messtin.nio.reactor.core.worker.impl.DefaultDispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * The builder used to build a server.
 */
public final class ServerBuilder {
    private static final Logger logger = LogManager.getLogger(ServerBuilder.class);

    private static final int workerCount = Runtime.getRuntime().availableProcessors() * 2;
    private EventHandlerFactory eventHandlerFactory;

    private ServerBuilder(EventHandlerFactory eventHandlerFactory) {
        this.eventHandlerFactory = Preconditions.isNotNull(eventHandlerFactory, "eventHandlerFactory is null");
    }

    public static ServerBuilder build(EventHandlerFactory eventHandlerFactory) {
        return new ServerBuilder(eventHandlerFactory);
    }

    public ServerPromise bind(int port) throws IOException {
        return bind(new InetSocketAddress(port));
    }

    /**
     * Only build an object of {@link Dispatcher}, {@link Acceptor} and {@link ServerPromise}
     * but didn't call {@link LifeCycle#initialize()} of them.
     *
     * @param address
     * @return
     * @throws IOException
     */
    public ServerPromise bind(InetSocketAddress address) throws IOException {
        logger.info("Building Dispatcher.");
        Dispatcher dispatcher = new DefaultDispatcher(workerCount, eventHandlerFactory);

        logger.info("Building Acceptor.");
        Acceptor acceptor = new Acceptor(dispatcher, address);

        logger.info("Building ServerPromise.");
        ServerPromise promise = new ServerPromise(acceptor, AcceptorThreadFactory.newInstance());
        return promise;
    }
}
