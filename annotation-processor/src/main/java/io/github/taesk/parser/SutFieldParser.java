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

public class SutFieldParser {

    public FieldSpec invoke(TypeElement element) {
        return FieldSpec.builder(TypeName.get(element.asType()), "sut")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build();
    }
}
