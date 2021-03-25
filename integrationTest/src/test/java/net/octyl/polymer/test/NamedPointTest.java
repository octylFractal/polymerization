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
