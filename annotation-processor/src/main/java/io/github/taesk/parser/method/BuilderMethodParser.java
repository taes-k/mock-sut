package io.github.taesk.parser.method;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeVariableName;
import io.github.taesk.parser.Parser;

public class BuilderMethodParser implements Parser<MethodSpec> {
    private final String generateClassName;

    public BuilderMethodParser(String generateClassName) {
        this.generateClassName = generateClassName;
    }

    public MethodSpec invoke() {
        String builderClassName = String.format("%sBuilder", generateClassName);

        return MethodSpec.methodBuilder("builder")
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .returns(TypeVariableName.get(builderClassName))
            .addStatement(String.format("return new %s()", builderClassName))
            .build();
    }
}