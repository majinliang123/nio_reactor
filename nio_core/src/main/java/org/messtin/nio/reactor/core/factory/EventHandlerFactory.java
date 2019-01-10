package org.messtin.nio.reactor.core.factory;

import org.messtin.nio.reactor.core.handler.EventHandler;

/**
 * The factory to create {@link EventHandler}
 */
public interface EventHandlerFactory {

    EventHandler create();
}
