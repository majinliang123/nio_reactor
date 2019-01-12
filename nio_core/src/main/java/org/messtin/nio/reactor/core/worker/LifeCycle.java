package org.messtin.nio.reactor.core.worker;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * The life cycle of a object.
 * All classes which will initialize and close some resource should implement this interface.
 */
public interface LifeCycle {

    enum Status {
        INACTIVE, ACTIVE, SHUTTING_DOWN, SHUT_DOWN;
    }

    /**
     * When run {@link #initialize()}, the status will change as:
     * {@link Status#INACTIVE} --> {@link Status#ACTIVE}
     *
     * @throws IOException
     */
    void initialize() throws IOException;

    /**
     * When run {@link #close()}, the status will change as:
     * {@link Status#ACTIVE} --> {@link Status#SHUTTING_DOWN}
     */
    void close();

    Status getStatus();
}
