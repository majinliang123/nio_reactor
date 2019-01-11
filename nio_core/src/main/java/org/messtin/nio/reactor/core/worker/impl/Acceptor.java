package org.messtin.nio.reactor.core.worker.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messtin.nio.reactor.core.Status;
import org.messtin.nio.reactor.core.worker.Dispatcher;
import org.messtin.nio.reactor.core.worker.LifeCycle;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Listen on {@link java.nio.channels.ServerSocketChannel},
 * if there are any connection, will call {@link org.messtin.nio.reactor.core.worker.Dispatcher#dispatch(SocketChannel)}
 * to dispatch to other workers.
 */
public class Acceptor implements LifeCycle, Runnable {

    private static Logger logger = LogManager.getLogger(Acceptor.class);

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Dispatcher dispatcher;
    private InetSocketAddress address;
    private volatile Status status = Status.INACTIVE;

    public Acceptor(Dispatcher dispatcher, InetSocketAddress address) throws IOException {
        this.dispatcher = dispatcher;
        this.address = address;
        this.selector = Selector.open();
        this.serverSocketChannel = buildServerSocketChannel();
    }

    private ServerSocketChannel buildServerSocketChannel() throws IOException {
        ServerSocketChannel newChannel = ServerSocketChannel.open();
        newChannel.configureBlocking(false);
        return newChannel;
    }

    @Override
    public void run() {
        logger.info("Start initialize Acceptor.");
        status = Status.ACTIVE;
        initialize();
        logger.info("Complete initialize Acceptor.");
    }

    private void processEvents(int count) throws IOException, InterruptedException {
        if (count > 0){
            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                processEvent(key);
            }
            keys.clear();
        }
    }

    private void processEvent(SelectionKey key) throws IOException, InterruptedException {
        if (key.isValid()) {
            SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
            socketChannel.socket().setTcpNoDelay(true);
            socketChannel.socket().setKeepAlive(false);
            dispatcher.dispatch(socketChannel);
        }
    }

    @Override
    public void initialize() {
        try {
            logger.info("Start initialize server socket channel.");
            serverSocketChannel.socket().bind(address);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            dispatcher.initialize();

            while (status == Status.ACTIVE) {
                int count = selector.select();
                processEvents(count);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void close() {
        status = Status.SHUTTING_DOWN;
        dispatcher.close();
    }

    @Override
    public void await() throws InterruptedException {
        dispatcher.await();
        status = Status.SHUT_DOWN;
    }

    @Override
    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        dispatcher.await(timeout, unit);
        status = Status.SHUT_DOWN;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
