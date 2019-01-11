package org.messtin.nio.reactor.core.worker.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messtin.nio.reactor.core.Status;
import org.messtin.nio.reactor.core.factory.EventHandlerFactory;
import org.messtin.nio.reactor.core.factory.impl.ProcessorThreadFactory;
import org.messtin.nio.reactor.core.worker.Dispatcher;
import org.messtin.nio.reactor.core.worker.Processor;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultDispatcher implements Dispatcher {
    private static final Logger logger = LogManager.getLogger(DefaultDispatcher.class);

    private volatile Status status = Status.INACTIVE;
    private int workersCount;
    private EventHandlerFactory eventHandlerFactory;
    private List<Processor> processors;
    private List<Thread> processorThreads;
    private AtomicInteger counter = new AtomicInteger(0);

    public DefaultDispatcher(int workersCount, EventHandlerFactory eventHandlerFactory) {

        this.workersCount = workersCount;
        this.eventHandlerFactory = eventHandlerFactory;
        processors = new ArrayList<>(workersCount);
        processorThreads = new ArrayList<>();
    }

    @Override
    public void dispatch(SocketChannel socketChannel) throws InterruptedException {
        int index = counter.getAndIncrement() % workersCount;

        logger.info("Dispatch socket channel to processor thread {}.", processorThreads.get(index).getName());
        processors.get(index).process(socketChannel);
    }

    @Override
    public void initialize() throws IOException {
        logger.info("Start initialize Default Dispatcher.");
        for (int i = 0; i < workersCount; i++) {
            Processor processor = new DefaultProcessor(eventHandlerFactory.create());
            Thread processorThread = ProcessorThreadFactory.newInstance().create(processor);
            processorThread.start();

            processors.add(processor);
            processorThreads.add(processorThread);
        }
        logger.info("Default Dispatcher started {} processor", processors.size());
    }

    @Override
    public void close() {

    }

    @Override
    public void await() {

    }

    @Override
    public void await(long timeout, TimeUnit unit) throws InterruptedException {

    }

    @Override
    public Status getStatus() {
        return status;
    }
}
