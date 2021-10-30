package io.github.taesk.parser;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockFieldParser {

    public List<FieldSpec> invoke(TypeElement element) {
        var fieldSpecList = new ArrayList<FieldSpec>();
        var constructorCount = element.getEnclosedElements().stream()
                .filter(it -> it.getKind() == ElementKind.CONSTRUCTOR && it.getModifiers().contains(Modifier.PUBLIC))
                .count();

        if (constructorCount < 1)
            return List.of();

        if (constructorCount == 1) {
            var fieldSpecs = element.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.CONSTRUCTOR && it.getModifiers().contains(Modifier.PUBLIC))
                    .flatMap(it -> ((ExecutableElement) it).getParameters().stream())
                    .map(it -> {
                                var paramName = it.getSimpleName().toString();
                                var paramType = TypeName.get(it.asType());

                                return FieldSpec.builder(paramType, paramName)
                                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                        .build();
                            }
                    ).collect(Collectors.toList());

            fieldSpecList.addAll(fieldSpecs);
        }

        if (constructorCount > 1) {

        }

        return fieldSpecList;
    }
}
