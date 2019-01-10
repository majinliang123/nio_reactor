package org.messtin.nio.reactor.core.worker.impl;

import org.messtin.nio.reactor.core.Status;
import org.messtin.nio.reactor.core.factory.EventHandlerFactory;
import org.messtin.nio.reactor.core.worker.Dispatcher;

import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class DefaultDispatcher implements Dispatcher {

    private volatile Status status = Status.INACTIVE;

    public DefaultDispatcher(int workersCount, EventHandlerFactory eventHandlerFactory) {

    }

    @Override
    public void dispatch(SocketChannel socketChannel) {

    }

    @Override
    public void initialize() {

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
