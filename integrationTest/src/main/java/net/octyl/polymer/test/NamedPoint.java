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
