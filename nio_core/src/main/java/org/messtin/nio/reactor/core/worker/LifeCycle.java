package org.messtin.nio.reactor.core.worker;

import org.messtin.nio.reactor.core.Status;

import java.util.concurrent.TimeUnit;

/**
 * The life cycle of a object.
 * All classes which will initialize and close some resource should implement this interface.
 */
public interface LifeCycle {

    void initialize();

    void close();

    /**
     * When we try to close a object, it may be need some time.
     * So we use {@link #await()} to block the thread until the object is closed.
     */
    void await() throws InterruptedException;

    void await(final long timeout, final TimeUnit unit) throws InterruptedException;

    Status getStatus();
}
