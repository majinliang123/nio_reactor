package org.messtin.nio.reactor.core.worker;

import java.nio.channels.SocketChannel;

/**
 * dispatch channel to every workers.
 */
public interface Dispatcher extends LifeCycle{

    void dispatch(SocketChannel socketChannel) throws InterruptedException;
}
