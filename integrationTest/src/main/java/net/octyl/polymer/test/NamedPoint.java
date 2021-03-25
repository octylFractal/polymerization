package net.octyl.polymer.test;

import net.octyl.polymer.PolymerizeApi;

public record NamedPoint(String name, int x, int y) {
    public static Builder builder() {
        return new PolymerizeImplNamedPoint_Builder();
    }

    @PolymerizeApi
    interface Builder {
        Builder name(String value);

        Builder x(int x);

        Builder y(int y);

        NamedPoint build();
    }
}
