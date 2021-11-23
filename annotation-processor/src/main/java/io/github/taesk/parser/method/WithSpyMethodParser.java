package io.github.taesk.parser.method;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeVariableName;
import io.github.taesk.parser.Parser;

public class WithSpyMethodParser implements Parser<List<MethodSpec>> {
    private final String originClassName;
    private final String generatedClassName;
    private final List<FieldSpec> fieldSpecs;

    public WithSpyMethodParser(String originClassName, String generatedClassName, List<FieldSpec> fieldSpecs) {
        this.originClassName = originClassName;
        this.generatedClassName = generatedClassName;
        this.fieldSpecs = fieldSpecs;
    }

    public List<MethodSpec> invoke() {

        CodeBlock sutFieldInitStatement =
            CodeBlock.builder()
                .add("this.sut = new $T(", ClassName.bestGuess(originClassName))
                .add(fieldSpecs.stream()
                    .map(it -> CodeBlock.of("$N", it))
                    .reduce((a, b) -> CodeBlock.join(Arrays.asList(a, b), ", "))
                    .orElseGet(() -> CodeBlock.of(""))
                )
                .add(");\n")
                .build();

        return fieldSpecs.stream()
            .map(it -> MethodSpec.methodBuilder(String.format("withSpy%s", StringUtils.capitalize(it.name)))
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeVariableName.get(generatedClassName))
                .addStatement(
                    CodeBlock.builder()
                        .add("this.$N = $T.spy($T.class);\n", it, Mockito.class, it.type)
                        .add(sutFieldInitStatement)
                        .add("return this")
                        .build())
                .build())
            .collect(Collectors.toList());
    }
}