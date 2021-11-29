package io.github.taesk.parser.clazz;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.github.taesk.parser.Parser;
import io.github.taesk.parser.field.MockFieldParser;

public class BuilderClassParser implements Parser<TypeSpec> {
    private final TypeElement element;
    private final MockFieldParser mockFieldParser;
    private final String generateClassName;

    public BuilderClassParser(TypeElement element, MockFieldParser mockFieldParser, String generateClassName) {
        this.element = element;
        this.mockFieldParser = mockFieldParser;
        this.generateClassName = generateClassName;
    }

    public TypeSpec invoke() {
        String builderClassName = String.format("%sBuilder", generateClassName);

        List<FieldSpec> mockFieldSpecs = mockFieldParser.invoke().stream()
            .map(it ->
                it.toBuilder()
                    .initializer("$T.mock($T.class)", Mockito.class, it.type)
                    .build()
            ).collect(Collectors.toList());
        
        List<MethodSpec> setterMethodSpecs = mockFieldSpecs.stream()
            .map(it -> MethodSpec.methodBuilder(it.name)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeVariableName.get(builderClassName))
                .addParameter(it.type, it.name)
                .addStatement(
                    CodeBlock.builder()
                        .add("this.$N = $N;\n", it, Mockito.class, it.type)
                        .add("return this")
                        .build())
                .build()
            ).collect(Collectors.toList());
        List<MethodSpec> withSpyMethodSpecs = mockFieldSpecs.stream()
            .map(it -> MethodSpec.methodBuilder(String.format("withSpy%s", StringUtils.capitalize(it.name)))
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeVariableName.get(builderClassName))
                .addParameter(it.type, it.name)
                .addStatement(
                    CodeBlock.builder()
                        .add("this.$N = $T.spy($T.class);\n", it, Mockito.class, it.type)
                        .add("return this")
                        .build())
                .build()
            ).collect(Collectors.toList());

        CodeBlock sutFieldInitStatement =
            CodeBlock.builder()
                .add("this.sut = new $T(", ClassName.get(element))
                .add(mockFieldSpecs.stream()
                    .map(it -> CodeBlock.of("$N", it))
                    .reduce((a, b) -> CodeBlock.join(Arrays.asList(a, b), ", "))
                    .orElseGet(() -> CodeBlock.of(""))
                )
                .add(")")
                .build();
        MethodSpec buildMethodSpec = MethodSpec.methodBuilder("build")
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeVariableName.get(generateClassName))
            .addCode(sutFieldInitStatement)
            .build();

        return TypeSpec.classBuilder(builderClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addFields(mockFieldSpecs)
            .addMethods(setterMethodSpecs)
            .addMethods(withSpyMethodSpecs)
            .addMethod(buildMethodSpec)
            .build();
    }
}
