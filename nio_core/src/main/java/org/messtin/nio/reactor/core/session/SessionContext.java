package org.messtin.nio.reactor.core.session;

import org.messtin.nio.reactor.core.worker.Processor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link SessionContext} is not thread safe,
 * please make sure there is only one thread use it.
 */
public class SessionContext {

    private SelectionKey key;
    private Processor processor;
    private SocketChannel socketChannel;
    private Map<AttributeKey<?>, Object> attributeMap;

    private boolean closed = false;

    public SessionContext(SelectionKey key, Processor processor) {
        this.key = key;
        this.processor = processor;
        this.socketChannel = (SocketChannel) key.channel();
        this.attributeMap = new HashMap<>();
    }

    public void close() throws IOException {
        if (!closed) {
            closed = true;
            key.cancel();
            socketChannel.close();
            processor.queueClosedChannel(this);
            if (key.selector().isOpen()) {
                key.selector().wakeup();
            }
        }
    }

    public void interestOp(int op) {
        key.interestOps(op);
    }

    public <T> T getAttribute(AttributeKey<T> attributeKey) {
        return attributeKey.cast(attributeMap.get(attributeKey));
    }

    public <T> void setAttribute(AttributeKey<T> attributeKey, T value) {
        attributeMap.put(attributeKey, value);
    }

    public SocketChannel channel() {
        return socketChannel;
    }

    public SocketAddress remoteAddress() {
        return socketChannel.socket().getRemoteSocketAddress();
    }

    public SocketAddress localAddress() {
        return socketChannel.socket().getLocalSocketAddress();
    }
}
