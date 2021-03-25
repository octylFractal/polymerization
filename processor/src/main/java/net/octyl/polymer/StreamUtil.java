package net.octyl.polymer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StreamUtil {

    /**
     * Convenience function for doing a check & cast to a type. Intended for use with
     * {@link Stream#mapMulti(BiConsumer)}.
     *
     * @param type the type to check if elements are an instance of
     * @param <T> the parameter matching {@code type}
     * @return a function compatible with {@code Stream.mapMulti}
     */
    public static <T> BiConsumer<Object, Consumer<T>> isInstance(Class<T> type) {
        return (v, downstream) -> {
            if (type.isInstance(v)) {
                downstream.accept(type.cast(v));
            }
        };
    }

    private StreamUtil() {
    }
}
