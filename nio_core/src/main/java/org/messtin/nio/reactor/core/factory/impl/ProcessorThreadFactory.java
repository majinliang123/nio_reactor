package org.messtin.nio.reactor.core.factory.impl;

import org.messtin.nio.reactor.core.factory.ThreadFactory;
import org.messtin.nio.reactor.core.util.Preconditions;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link ThreadFactory} to create Processor thread.
 */
public final class ProcessorThreadFactory implements ThreadFactory {

    private static AtomicInteger index = new AtomicInteger(0);
    private static final String name = "Processor-Thread-";
    private static volatile ProcessorThreadFactory instance;

    private ProcessorThreadFactory() {
    }


    @Override
    public Thread create(Runnable r) {
        Preconditions.isNotNull(r, "Param r is null");
        return new Thread(r, name + index.getAndIncrement());
    }


    public static ProcessorThreadFactory newInstance() {
        if (instance == null) {
            synchronized (ProcessorThreadFactory.class) {
                if (instance == null) {
                    instance = new ProcessorThreadFactory();
                }
            }
        }
        return instance;
    }
}
