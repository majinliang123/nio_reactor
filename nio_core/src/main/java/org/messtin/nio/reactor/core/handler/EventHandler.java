package org.messtin.nio.reactor.core.handler;

import org.messtin.nio.reactor.core.SessionContext;

public interface EventHandler {

    /**
     * We a connection connect with server.
     *
     * @param session
     */
    void connect(SessionContext session);

    /**
     * When the input of the connection is ready.
     *
     * @param session
     */
    void inputReady(SessionContext session);

    /**
     * When the output of the connection is ready.
     *
     * @param session
     */
    void outputReady(SessionContext session);

    /**
     * When will close the connection.
     *
     * @param session
     */
    void disconnect(SessionContext session);
}
