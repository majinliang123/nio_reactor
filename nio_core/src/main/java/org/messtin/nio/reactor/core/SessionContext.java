package org.messtin.nio.reactor.core;

import org.messtin.nio.reactor.core.worker.Processor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SessionContext {

    private SelectionKey key;
    private Processor processor;
    private SocketChannel socketChannel;

    public SessionContext(SelectionKey key, Processor processor) {
        this.key = key;
        this.processor = processor;
        this.socketChannel = (SocketChannel) key.channel();
    }

    public void close() throws IOException {
        key.cancel();
        socketChannel.close();
    }

    public void interestOp(int op){
        System.out.println("Update interest op to " + op);
        key.interestOps(op);
    }

    public SocketChannel channel(){
        return socketChannel;
    }
}
