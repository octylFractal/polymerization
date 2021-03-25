package net.octyl.polymer;

import javax.annotation.processing.Generated;
import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSetMultimap;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

@AutoService(Processor.class)
public class PolymerizationProcessor extends BasicAnnotationProcessor {
    private final @Nullable ZonedDateTime forcedDate;

    // used by the service loader
    @SuppressWarnings("unused")
    public PolymerizationProcessor() {
        this(null);
    }

    /**
     * This processor constructor is to allow consistent output.
     */
    @VisibleForTesting
    public PolymerizationProcessor(@Nullable ZonedDateTime forcedDate) {
        this.forcedDate = forcedDate;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // Require at least 16
        var sourceVersion = SourceVersion.RELEASE_16;
        if (sourceVersion.compareTo(SourceVersion.latestSupported()) < 0) {
            sourceVersion = SourceVersion.latestSupported();
        }
        return sourceVersion;
    }

    @Override
    protected Iterable<? extends Step> steps() {
        return List.of(new Step() {
            @Override
            public Set<String> annotations() {
                return Set.of(PolymerizeApi.class.getCanonicalName());
            }

            @Override
            public Set<? extends Element> process(ImmutableSetMultimap<String, Element> elementsByAnnotation) {
                for (var element : elementsByAnnotation.get(PolymerizeApi.class.getCanonicalName())) {
                    try {
                        processElement(MoreElements.asType(element));
                    } catch (DiagnosableException e) {
                        e.print(processingEnv.getMessager());
                    }
                }
                return Set.of();
            }

            private void processElement(TypeElement builder) {
                var builderData = BuilderData.derive(builder);
                var recordType = builderData.info().recordType();
                if (recordType.getKind() != ElementKind.RECORD) {
                    throw new DiagnosableException(
                        "PolymerizeApi target (" + recordType + ") is not a record",
                        builder
                    );
                }
                for (var component : recordType.getRecordComponents()) {
                    var matchingSetter = builderData.findSetter(component);
                    if (matchingSetter == null) {
                        throw new DiagnosableException(
                            "Builder is missing a setter for " + component,
                            builder
                        );
                    }
                }

                var spec = generateImplementation(builderData);
                try {
                    JavaFile.builder(builderData.info().implName().packageName(), spec).build()
                        .writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    throw new DiagnosableException(
                        "Error writing builder", null, e
                    );
                }
            }

            private TypeSpec generateImplementation(BuilderData builderData) {
                var spec = TypeSpec.classBuilder(builderData.info().implName())
                    .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", PolymerizationProcessor.class.getCanonicalName())
                        .addMember("date", "$S",
                            DateTimeFormatter.ISO_DATE_TIME.format(
                                Objects.requireNonNullElse(
                                    forcedDate,
                                    ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
                                )
                            )
                        )
                        .build())
                    .addModifiers(Modifier.FINAL)
                    .addSuperinterface(builderData.info().builderName());

                for (var component : builderData.info().components()) {
                    spec.addField(
                        TypeName.get(component.element().asType())
                            .annotated(component.element().getAnnotationMirrors()
                                .stream()
                                .map(AnnotationSpec::get)
                                .toList()),
                        component.name(),
                        Modifier.PRIVATE
                    );

                    spec.addMethod(assembleSetterImpl(builderData, component));
                }

                spec.addMethod(assembleBuildImpl(builderData));

                return spec.build();
            }

            private MethodSpec assembleSetterImpl(BuilderData builderData, BuilderData.ComponentInfo component) {
                var setter = Objects.requireNonNull(builderData.findSetter(component.element()));
                var setterParamName = setter.getParameters().get(0).getSimpleName().toString();
                var code = CodeBlock.builder();
                // null check non-null non-primitive properties
                if (component.nullableAnnotation() == null && !component.element().asType().getKind().isPrimitive()) {
                    code.beginControlFlow("if ($N == null)", setterParamName)
                        // Share the common part of the string via concat
                        .addStatement(
                            "throw new $T($S.concat($S).concat($S).concat($S))",
                            IllegalArgumentException.class,
                            "'", component.name(), "'",
                            " cannot be null"
                        )
                        .endControlFlow();
                }
                code.addStatement("this.$N = $N", component.name(), setterParamName);
                code.addStatement("return this");
                return MethodSpec.overriding(setter)
                    .addCode(code.build())
                    .build();
            }

            private MethodSpec assembleBuildImpl(BuilderData builderData) {
                var buildMethod = builderData.findBuildMethod();
                if (buildMethod == null) {
                    throw new DiagnosableException(
                        "Builder is missing a build-like method",
                        builderData.info().builderType()
                    );
                }
                var code = CodeBlock.builder();

                // Figure out unset vars
                var missingCollector = "$missing";
                code.addStatement("$T $N = \"\"", String.class, missingCollector);
                for (var component : builderData.info().components()) {
                    if (component.nullableAnnotation() != null
                        || component.element().asType().getKind().isPrimitive()) {
                        continue;
                    }
                    code.beginControlFlow("if ($N == null)", component.name())
                        .addStatement(
                            // Share the common part of the string via concat
                            "$1N = $1N.concat($2S).concat($3S)",
                            missingCollector,
                            " ", component.name()
                        )
                        .endControlFlow();
                }
                code.beginControlFlow("if (!$N.isEmpty())", missingCollector)
                    .addStatement(
                        "throw new $T($S + $N)",
                        IllegalStateException.class,
                        "Missing required properties:", missingCollector
                    )
                    .endControlFlow();

                code.addStatement(
                    "return new $T($L)",
                    TypeName.get(buildMethod.getReturnType()),
                    builderData.info().components().stream()
                        .map(c -> CodeBlock.of("$N", c.name()))
                        .collect(CodeBlock.joining(", "))
                );

                return MethodSpec.overriding(buildMethod)
                    .addCode(code.build())
                    .build();
            }
        });
    }
}
