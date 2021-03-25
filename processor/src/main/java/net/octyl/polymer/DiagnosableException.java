package net.octyl.polymer;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import java.util.Objects;

import com.google.common.base.Throwables;
import org.jetbrains.annotations.Nullable;

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
