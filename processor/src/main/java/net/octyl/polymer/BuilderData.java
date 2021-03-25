package net.octyl.polymer;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.Nullable;

public class BuilderData {
    public static BuilderData derive(TypeElement builder) {
        var recordType = decodeAnnotations(builder);
        var builderClassName = TypeNameUtil.rawType(TypeName.get(builder.asType()));
        var builderMethodIndex = builder.getEnclosedElements().stream()
            .mapMulti(StreamUtil.isInstance(ExecutableElement.class))
            .collect(Multimaps.toMultimap(
                e -> e.getSimpleName().toString(),
                Function.identity(),
                MultimapBuilder.linkedHashKeys().arrayListValues()::build
            ));
        var components = recordType.getRecordComponents().stream()
            .map(component -> new ComponentInfo(
                component.getSimpleName().toString(),
                component,
                Stream.concat(
                    component.getAnnotationMirrors().stream(),
                    component.asType().getAnnotationMirrors().stream()
                )
                    .filter(am -> {
                        var annoTypeName = am.getAnnotationType().asElement().getSimpleName().toString();
                        return annoTypeName.equals("Nullable");
                    })
                    .map(AnnotationSpec::get)
                    .findFirst()
                    .orElse(null)
            ))
            .toList();
        return new BuilderData(builder, new Info(
            builder,
            recordType,
            builderClassName,
            builderClassName.topLevelClassName().peerClass(
                "PolymerizeImpl" + String.join("_", builderClassName.simpleNames())
            ),
            components
        ), builderMethodIndex);
    }

    public record Info(
        TypeElement builderType,
        TypeElement recordType,
        ClassName builderName,
        ClassName implName,
        List<ComponentInfo> components
    ) {
    }

    public record ComponentInfo(
        String name,
        RecordComponentElement element,
        @Nullable AnnotationSpec nullableAnnotation
    ) {
    }

    private static TypeElement decodeAnnotations(Element element) {
        var annotation = MoreElements.getAnnotationMirror(element, PolymerizeApi.class)
            .toJavaUtil().orElseThrow(() -> new IllegalStateException("PolymerizeApi missing"));
        var resultValue = AnnotationMirrors.getAnnotationValue(annotation, "result")
            .getValue();
        if (!(resultValue instanceof TypeMirror typeMirror)) {
            throw new IllegalStateException("Not a type mirror: " + resultValue);
        }
        Element resultingElement;
        if (MoreTypes.isTypeOf(Void.class, typeMirror)) {
            resultingElement = element.getEnclosingElement();
            if (resultingElement.getKind() != ElementKind.RECORD) {
                throw new DiagnosableException(
                    "Builder is not enclosed by a record, please specify using `@PolymerizeApi(result = ...)`",
                    element
                );
            }
        } else {
            resultingElement = MoreTypes.asElement(typeMirror);
            if (resultingElement.getKind() != ElementKind.RECORD) {
                throw new DiagnosableException(
                    "@PolymerizeApi's `result` type is not a record",
                    element
                );
            }
        }
        return MoreElements.asType(resultingElement);
    }

    private final TypeElement builder;
    private final Info info;
    private final ListMultimap<String, ExecutableElement> builderMethodIndex;

    private BuilderData(TypeElement builder, Info info, ListMultimap<String, ExecutableElement> builderMethodIndex) {
        this.info = info;
        this.builder = builder;
        this.builderMethodIndex = builderMethodIndex;
    }

    public Info info() {
        return info;
    }

    public @Nullable ExecutableElement findBuildMethod() {
        return builderMethodIndex.values().stream()
            .filter(element ->
                // no parameters
                element.getParameters().isEmpty()
                    // and returns the record type
                    && MoreTypes.asElement(element.getReturnType()).equals(info().recordType())
            )
            .findAny()
            .orElse(null);
    }

    public @Nullable ExecutableElement findSetter(RecordComponentElement component) {
        var byName = builderMethodIndex.get(component.getSimpleName().toString());

        return byName.stream()
            .filter(element ->
                // 1 parameter
                element.getParameters().size() == 1
                    // of same type
                    && element.getParameters().get(0).asType().equals(component.asType())
                    // and returns the builder type
                    && element.getReturnType().equals(builder.asType())
            )
            .findAny()
            .orElse(null);
    }
}
