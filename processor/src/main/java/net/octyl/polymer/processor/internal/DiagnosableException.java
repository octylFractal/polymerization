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

import com.google.common.base.Throwables;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.Objects;

public class DiagnosableException extends RuntimeException {
    private final @Nullable Element element;

    public DiagnosableException(String message) {
        this(message, null, null);
    }

    public DiagnosableException(String message, @Nullable Element element) {
        this(message, element, null);
    }

    public DiagnosableException(String message,
                                @Nullable Element element,
                                @Nullable Throwable cause) {
        super(Objects.requireNonNull(message), cause);
        this.element = element;
    }

    public void print(Messager messager) {
        var message = getCause() == null
            ? getMessage()
            : getMessage() + ": " + Throwables.getStackTraceAsString(getCause());
        if (element == null) {
            messager.printMessage(
                Diagnostic.Kind.ERROR, message
            );
        } else {
            messager.printMessage(
                Diagnostic.Kind.ERROR, message, element
            );
        }
    }
}
