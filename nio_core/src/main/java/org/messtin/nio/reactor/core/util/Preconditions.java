package org.messtin.nio.reactor.core.util;

/**
 * The util to check if the param match requirement, otherwise will throw a {@link RuntimeException}
 */
public final class Preconditions {
    private Preconditions() {
    }

    public static <T> T isNotNull(T t, Object message) {
        if (t == null) {
            throw new NullPointerException(String.valueOf(message));
        }
        return t;
    }
}
