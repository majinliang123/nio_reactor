package org.messtin.nio.reactor.core.session;

import org.messtin.nio.reactor.core.util.Preconditions;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The attribute of {@link SessionContext}
 *
 * @param <T>
 */
public class AttributeKey<T> {

    private static Set<String> NAMES = ConcurrentHashMap.newKeySet();
    private final String name;
    private final Class<T> type;

    public AttributeKey(final String name, final Class<T> type) {
        Preconditions.isNotNull(name, "name is null");
        Preconditions.isNotNull(type, "type is null");

        validateNameUniqueness(name);
        this.name = name;
        this.type = type;
    }

    public T cast(Object o) {
        return type.cast(o);
    }

    public static void validateNameUniqueness(final String name) {

        if (!NAMES.add(name)) {
            throw new IllegalArgumentException("AttributeKey name " + name + " already existed.");
        }
    }
}
