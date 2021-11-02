package io.github.taesk.parser.field;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import io.github.taesk.parser.Parser;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class SutFieldParser implements Parser<FieldSpec> {
    private final TypeElement element;

    public SutFieldParser(TypeElement element) {
        this.element = element;
    }

    public FieldSpec invoke() {
        return FieldSpec.builder(TypeName.get(element.asType()), "sut")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }
}
