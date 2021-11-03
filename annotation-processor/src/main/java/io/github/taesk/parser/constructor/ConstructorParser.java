package io.github.taesk.parser.constructor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import io.github.taesk.parser.Parser;
import io.github.taesk.parser.field.MockFieldParser;
import org.mockito.Mockito;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.List;

public class ConstructorParser implements Parser<MethodSpec> {
    private final TypeElement element;
    private final MockFieldParser mockFieldParser;

    public ConstructorParser(TypeElement element, MockFieldParser mockFieldParser) {
        this.element = element;
        this.mockFieldParser = mockFieldParser;
    }

    public MethodSpec invoke() {
        List<FieldSpec> mockFieldSpecs = mockFieldParser.invoke();
        CodeBlock mockFieldInitStatement = mockFieldSpecs.stream()
                .map(it -> CodeBlock.of("this.$N = $T.mock($T.class);", it, Mockito.class, it.type))
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
                .addModifiers(Modifier.PUBLIC)
                .addStatement(CodeBlock.join(Arrays.asList(mockFieldInitStatement, sutFieldInitStatement), "\n"))
                .build();
    }
}
