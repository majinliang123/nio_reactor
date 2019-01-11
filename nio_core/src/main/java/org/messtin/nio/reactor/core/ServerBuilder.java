package org.messtin.nio.reactor.core;

import org.messtin.nio.reactor.core.factory.EventHandlerFactory;
import org.messtin.nio.reactor.core.factory.impl.AcceptorThreadFactory;
import org.messtin.nio.reactor.core.util.Preconditions;
import org.messtin.nio.reactor.core.worker.Dispatcher;
import org.messtin.nio.reactor.core.worker.impl.Acceptor;
import org.messtin.nio.reactor.core.worker.impl.DefaultDispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * The builder used to build a server.
 */
public final class ServerBuilder {

    private static final int workerCount = Runtime.getRuntime().availableProcessors() * 2;
    private EventHandlerFactory eventHandlerFactory;

    private ServerBuilder(EventHandlerFactory eventHandlerFactory) {
        this.eventHandlerFactory = Preconditions.isNotNull(eventHandlerFactory, "eventHandlerFactory is null");
    }

    public static ServerBuilder build(EventHandlerFactory eventHandlerFactory) {
        return new ServerBuilder(eventHandlerFactory);
    }

    public ServerPromise bind(int port) {
        return bind(new InetSocketAddress(port));
    }

    public ServerPromise bind(InetSocketAddress address) {
        Dispatcher dispatcher = new DefaultDispatcher(workerCount, eventHandlerFactory);
        try {
            Acceptor acceptor = new Acceptor(dispatcher, address);
            ServerPromise promise = new ServerPromise(acceptor, AcceptorThreadFactory.newInstance());
            return promise;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
