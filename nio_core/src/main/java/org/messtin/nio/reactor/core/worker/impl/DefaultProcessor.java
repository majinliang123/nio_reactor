package org.messtin.nio.reactor.core.worker.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.messtin.nio.reactor.core.session.SessionContext;
import org.messtin.nio.reactor.core.handler.EventHandler;
import org.messtin.nio.reactor.core.util.Preconditions;
import org.messtin.nio.reactor.core.worker.Processor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultProcessor extends Thread implements Processor {
    private static final Logger logger = LogManager.getLogger(DefaultProcessor.class);

    private Selector selector;
    private EventHandler eventHandler;
    private Queue<SocketChannel> newChannels;
    private Set<SessionContext> sessions;
    private Set<SessionContext> closedSessions;

    private volatile Status status = Status.INACTIVE;

    public DefaultProcessor(EventHandler eventHandler) throws IOException {
        Preconditions.isNotNull(eventHandler, "eventHandler is null.");
        this.eventHandler = eventHandler;
        initialize();
    }

    public void run() {
        status = Status.ACTIVE;
        logger.info("Processor {} is running.", Thread.currentThread().getName());

        try {
            while (status == Status.ACTIVE || !sessions.isEmpty()) {
                int readyCount = selector.select();
                logger.info("Selected {} selection keys from selector.", readyCount);

                if (status == Status.SHUTTING_DOWN) {
                    closeChannelQueue();
                    closeSessions();
                }

                if (readyCount > 0) {
                    processEvents(selector.selectedKeys());
                }

                processClosedChannel();

                if (status == Status.ACTIVE) {
                    processNewChannel();
                }

            }
        } catch (Exception e) {
            logger.error(e);
        }
        logger.info("Processor {} is shut down.", Thread.currentThread().getName());
    }

    private void closeChannelQueue() throws IOException {
        for (SocketChannel newChannel : newChannels) {
            newChannel.close();
        }
    }

    private void closeSessions() throws IOException {
        for (SessionContext session : sessions) {
            session.close();
        }
    }

    private void processEvents(Set<SelectionKey> keys) {
        keys.forEach(this::processEvent);
        keys.clear();
    }

    private void processEvent(SelectionKey key) {
        if (key.isValid()) {
            SessionContext session = (SessionContext) key.attachment();
            if (key.isReadable()) {
                eventHandler.inputReady(session);
            }
            if (key.isWritable()) {
                eventHandler.outputReady(session);
            }
        }
    }

    private void processNewChannel() throws IOException {
        SocketChannel socketChannel;
        while ((socketChannel = newChannels.poll()) != null) {
            logger.info("Register {} to selector.", socketChannel);
            SelectionKey key;
            socketChannel.configureBlocking(false);
            key = socketChannel.register(selector, 0);

            logger.info("Create Session Context for {}.", socketChannel);
            SessionContext session = new SessionContext(key, this);
            sessions.add(session);
            key.attach(session);
            sessionCreated(session);
        }
    }

    private void sessionCreated(SessionContext session) {
        eventHandler.connect(session);
    }

    public void queueClosedChannel(SessionContext session) {
        closedSessions.add(session);
    }

    private void processClosedChannel() {
        for (SessionContext session : closedSessions) {
            eventHandler.disconnect(session);
        }
        closedSessions.clear();
    }

    @Override
    public void process(SocketChannel socketChannel) {
        Preconditions.isNotNull(socketChannel, "socketChannel is null");
        logger.info("Processor accept new socket channel {}", socketChannel);
        newChannels.add(socketChannel);
        selector.wakeup();
    }

    @Override
    public void initialize() throws IOException {
        logger.info("Start initialize DefaultProcessor.");
        selector = Selector.open();
        newChannels = new ConcurrentLinkedQueue<>();
        sessions = new HashSet<>();
        closedSessions = new HashSet<>();
    }

    @Override
    public void close() {
        status = Status.SHUTTING_DOWN;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
