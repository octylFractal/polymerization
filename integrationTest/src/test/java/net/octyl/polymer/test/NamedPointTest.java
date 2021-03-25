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

package net.octyl.polymer.test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;


import org.junit.jupiter.api.Test;

public class NamedPointTest {
    @Test
    void namedPointConstruction() {
        assertThat(
            NamedPoint.builder()
                .name("Near Origin")
                .x(0)
                .y(1)
                .build()
        ).isEqualTo(new NamedPoint("Near Origin", 0, 1));
    }

    @Test
    void componentsNotGiven() {
        assertAll(() -> {
            var ex = assertThrows(IllegalStateException.class, () -> NamedPoint.builder().build());
            assertThat(ex.getMessage()).contains(": name x y");
        });
        assertAll(() -> {
            var ex = assertThrows(IllegalStateException.class, () -> NamedPoint.builder().name("").build());
            assertThat(ex.getMessage()).contains(": x y");
        });
        assertAll(() -> {
            var ex = assertThrows(IllegalStateException.class, () -> NamedPoint.builder().x(0).build());
            assertThat(ex.getMessage()).contains(": name y");
        });
        assertAll(() -> {
            var ex = assertThrows(IllegalStateException.class, () -> NamedPoint.builder().y(0).build());
            assertThat(ex.getMessage()).contains(": name x");
        });
    }

    @Test
    void setterGivenNull() {
        assertAll(() -> {
            var ex = assertThrows(IllegalArgumentException.class, () -> NamedPoint.builder().name(null));
            assertThat(ex.getMessage()).contains("'name' cannot be null");
        });
    }
}
