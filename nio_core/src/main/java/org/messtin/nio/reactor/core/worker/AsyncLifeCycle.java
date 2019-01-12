package org.messtin.nio.reactor.core.worker;

/**
 * When the close operation is async, should implement this interface.
 */
public interface AsyncLifeCycle extends LifeCycle {
    /**
     * When we try to close a object, it may be need some time.
     * So we use {@link #await()} to block the thread until the object is closed.
     */
    void await() throws InterruptedException;
}
