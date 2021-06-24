/*
 * Copyright (c) Octavia Togami <https://octyl.net>
 * Copyright (c) contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.octyl.polymer.processor.internal;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StreamUtil {

    /**
     * Convenience function for doing a check &amp; cast to a type. Intended for use with
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
