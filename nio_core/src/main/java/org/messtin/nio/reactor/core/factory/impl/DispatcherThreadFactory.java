package org.messtin.nio.reactor.core.factory.impl;

import org.messtin.nio.reactor.core.factory.ThreadFactory;
import org.messtin.nio.reactor.core.util.Preconditions;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link ThreadFactory} to create Dispatcher thread.
 */
public final class DispatcherThreadFactory implements ThreadFactory {

    private static AtomicInteger index = new AtomicInteger(0);
    private static final String name = "Dispatcher-Thread-";
    private static volatile DispatcherThreadFactory instance;

    private DispatcherThreadFactory() {
    }


    @Override
    public Thread create(Runnable r) {
        Preconditions.isNotNull(r, "Param r is null");
        return new Thread(r, name + index.getAndIncrement());
    }


    public static DispatcherThreadFactory newInstance() {
        if (instance == null) {
            synchronized (DispatcherThreadFactory.class) {
                if (instance == null) {
                    instance = new DispatcherThreadFactory();
                }
            }
        }
        return instance;
    }
}
