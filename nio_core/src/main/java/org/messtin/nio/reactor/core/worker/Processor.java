package org.messtin.nio.reactor.core.worker;

import java.nio.channels.SocketChannel;

/**
 * The worker to process channel.
 */
public interface Processor extends Runnable, LifeCycle {
    void process(SocketChannel socketChannel) throws InterruptedException;
}
