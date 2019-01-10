package org.messtin.nio.reactor.core.factory;

/**
 * The factory to create {@link Thread}
 */
public interface ThreadFactory {

    Thread create(Runnable r);
}
