package io.github.taesk.parser;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MockFieldParser implements Parser<List<FieldSpec>> {
    private final TypeElement element;

    public MockFieldParser(TypeElement element) {
        this.element = element;
    }

    public List<FieldSpec> invoke() {
        ExecutableElement constructorElement;

        var constructorCount = element.getEnclosedElements().stream()
                .filter(it -> it.getKind() == ElementKind.CONSTRUCTOR && it.getModifiers().contains(Modifier.PUBLIC))
                .count();

        if (constructorCount < 1) {
            return List.of();
        }

        if (constructorCount == 1) {
            constructorElement = (ExecutableElement) element.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.CONSTRUCTOR && it.getModifiers().contains(Modifier.PUBLIC))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Constructor parse error"));
        } else {
            constructorElement = (ExecutableElement) element.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.CONSTRUCTOR && it.getModifiers().contains(Modifier.PUBLIC))
                    .max(Comparator.comparing(it -> ((ExecutableElement) it).getParameters().size()))
                    .orElseThrow(() -> new RuntimeException("Constructor parse error"));
        }

        return getFieldSpecs(constructorElement);
    }

    @NotNull
    private List<FieldSpec> getFieldSpecs(ExecutableElement constructorElement) {
        return constructorElement.getParameters().stream()
                .map(it -> {
                            var paramName = it.getSimpleName().toString();
                            var paramType = TypeName.get(it.asType());

                            return FieldSpec.builder(paramType, paramName)
                                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                    .build();
                        }
                ).collect(Collectors.toList());
    }
}
