package org.messtin.nio.reactor.core.factory.impl;

import org.messtin.nio.reactor.core.factory.ThreadFactory;
import org.messtin.nio.reactor.core.util.Preconditions;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The factory to create Acceptor thread.
 */
public final class AcceptorThreadFactory implements ThreadFactory {

    private static AtomicInteger index = new AtomicInteger(0);
    private static final String name = "Acceptor-Thread-";
    private static volatile AcceptorThreadFactory instance;

    private AcceptorThreadFactory() {
    }

    @Override
    public Thread create(Runnable r) {
        Preconditions.isNotNull(r, "Param r is null");
        return new Thread(r, name + index.getAndIncrement());
    }


    public static AcceptorThreadFactory newInstance() {
        if (instance == null) {
            synchronized (AcceptorThreadFactory.class) {
                if (instance == null) {
                    instance = new AcceptorThreadFactory();
                }
            }
        }
        return instance;
    }
}
