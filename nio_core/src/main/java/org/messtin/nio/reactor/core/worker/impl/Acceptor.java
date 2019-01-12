package org.messtin.nio.reactor.core.worker.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messtin.nio.reactor.core.util.Preconditions;
import org.messtin.nio.reactor.core.worker.AsyncLifeCycle;
import org.messtin.nio.reactor.core.worker.Dispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Listen on {@link java.nio.channels.ServerSocketChannel},
 * if there are any connection, will call {@link Dispatcher#dispatch(SocketChannel)} dispatch to Dispatcher.
 * <p>
 * {@link Acceptor} is used to listen the connection from client, and then dispatch to {@link Dispatcher}.
 */
public class Acceptor implements AsyncLifeCycle, Runnable {

    private static final Logger logger = LogManager.getLogger(Acceptor.class);

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Dispatcher dispatcher;
    private InetSocketAddress address;
    private volatile Status status = Status.INACTIVE;

    public Acceptor(Dispatcher dispatcher, InetSocketAddress address) throws IOException {
        Preconditions.isNotNull(dispatcher, "dispatcher is null.");
        Preconditions.isNotNull(address, "address is null.");
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
    }

    private void processEvents(final int count) throws IOException, InterruptedException {
        if (count > 0) {
            final Set<SelectionKey> keys = selector.selectedKeys();
            for (final SelectionKey key : keys) {
                processEvent(key);
            }
            keys.clear();
        }
    }

    private void processEvent(final SelectionKey key) throws IOException, InterruptedException {
        if (key.isValid()) {
            logger.info("Start process key={}.", key);
            if (key.isAcceptable()) {
                final SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                if (socketChannel != null) {
                    dispatcher.dispatch(socketChannel);
                }
            }
        }
    }

    @Override
    public void initialize() {
        try {
            logger.info("Start initialize server socket channel.");
            serverSocketChannel.socket().bind(address, 1000000);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            dispatcher.initialize();

            while (status == Status.ACTIVE) {
                final int count = selector.select();
                processEvents(count);
            }
        } catch (Exception e) {
            logger.fatal("Fail to initialize Acceptor");
            close();
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        logger.info("Start close Acceptor");
        status = Status.SHUTTING_DOWN;
        try {
            if (selector.isOpen()) {
                selector.keys().forEach(key -> {
                    try {
                        key.channel().close();
                    } catch (IOException e) {
                        // ignore
                    }
                });
                selector.close();
                logger.info("selector of Acceptor is closed.");
            }
        } catch (IOException e) {
            logger.error("Failed to close selector of Acceptor.");
            e.printStackTrace();
        }

        try {
            serverSocketChannel.close();
            logger.info("server socket channel of Acceptor is closed.");
        } catch (IOException e) {
            logger.error("Failed to close server socket channel of Acceptor.");
            e.printStackTrace();
        }

        dispatcher.close();
        logger.info("dispatcher is closed.");
    }

    @Override
    public void await() throws InterruptedException {
        dispatcher.await();
        status = Status.SHUT_DOWN;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
