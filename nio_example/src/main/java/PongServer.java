import org.messtin.nio.reactor.core.ServerBuilder;
import org.messtin.nio.reactor.core.ServerPromise;
import org.messtin.nio.reactor.core.SessionContext;
import org.messtin.nio.reactor.core.handler.EventHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;

public class PongServer {

    public static void main(String[] args){
        ServerPromise server = ServerBuilder.build(PongEventListener::new).bind(8080);
        server.start();
    }

    public static class PongEventListener implements EventHandler{

        private final ByteBuffer buffer = ByteBuffer.wrap("Pong...".getBytes(StandardCharsets.UTF_8));

        public void connect(final SessionContext session) {
            session.interestOp(SelectionKey.OP_WRITE);
        }

        @Override
        public void inputReady(final SessionContext session) {

        }

        @Override
        public void outputReady(final SessionContext session) {
            try {
                ByteBuffer b = buffer.duplicate();
                session.channel().write(b);
                if (b.hasRemaining()) {
                    b.compact();
                } else {
                    session.close();
                }
            } catch (final IOException ex) {
            }
        }

        @Override
        public void disconnect(final SessionContext session) {

        }
    }
}
