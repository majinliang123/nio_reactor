package org.messtin.nio.reactor.core.worker;

import java.nio.channels.SocketChannel;

/**
 * dispatch channel to every {@link Processor}.
 *
 * Dispatcher will not start a new thread, he will get {@link SocketChannel} from {@link org.messtin.nio.reactor.core.worker.impl.Acceptor}
 * and then dispatch channel to every {@link Processor}.
 *
 * Dispatcher is used to control the status and information of every {@link Processor}.
 */
public interface Dispatcher extends AsyncLifeCycle{

    void dispatch(SocketChannel socketChannel) throws InterruptedException;
}
