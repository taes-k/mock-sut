package io.github.taesk.parser.constructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import io.github.taesk.parser.Parser;
import io.github.taesk.parser.field.MockFieldParser;

public class AllParameterConstructorParser implements Parser<MethodSpec> {
    private final TypeElement element;
    private final MockFieldParser mockFieldParser;

    public AllParameterConstructorParser(TypeElement element, MockFieldParser mockFieldParser) {
        this.element = element;
        this.mockFieldParser = mockFieldParser;
    }

    public MethodSpec invoke() {
        List<FieldSpec> mockFieldSpecs = mockFieldParser.invoke();
        List<ParameterSpec> parameterSpecs = mockFieldSpecs.stream()
            .map(it -> ParameterSpec.builder(it.type, it.name).build())
            .collect(Collectors.toList());

        CodeBlock mockFieldInitStatement = mockFieldSpecs.stream()
            .map(it -> CodeBlock.of("this.$N = $N;", it, it))
            .reduce((a, b) -> CodeBlock.join(Arrays.asList(a, b), "\n"))
            .orElseGet(() -> CodeBlock.of(""));

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

        return MethodSpec.constructorBuilder()
            .addParameters(parameterSpecs)
            .addModifiers(Modifier.PUBLIC)
            .addStatement(CodeBlock.join(Arrays.asList(mockFieldInitStatement, sutFieldInitStatement), "\n"))
            .build();
    }
}
