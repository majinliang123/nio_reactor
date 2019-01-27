import org.messtin.nio.reactor.core.ServerBuilder;
import org.messtin.nio.reactor.core.ServerPromise;
import org.messtin.nio.reactor.core.handler.EventHandler;
import org.messtin.nio.reactor.core.session.SessionContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;

public class PongServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerPromise server = ServerBuilder.build(PongEventListener::new).bind(8080);
        server.start();
        System.out.println("Will close the server if you enter any word.");
        System.in.read();
        server.close();
        server.await();
    }

    public static class PongEventListener implements EventHandler {

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
            System.out.println("Closed: " + session.remoteAddress());
        }
    }
}
