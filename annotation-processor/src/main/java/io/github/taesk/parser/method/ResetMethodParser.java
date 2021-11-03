package io.github.taesk.parser.method;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import io.github.taesk.parser.Parser;
import org.mockito.Mockito;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;

public class ResetMethodParser implements Parser<MethodSpec> {
    private final List<FieldSpec> mockFieldSpecs;

    public ResetMethodParser(List<FieldSpec> fieldSpecs) {
        this.mockFieldSpecs = fieldSpecs;
    }

    public MethodSpec invoke() {
        CodeBlock resetMockStatement = mockFieldSpecs.stream()
                .map(it -> CodeBlock.of("$T.reset($N);", Mockito.class, it))
                .reduce((a, b) -> CodeBlock.join(Arrays.asList(a, b), "\n"))
                .orElseGet(() -> CodeBlock.of(""));

        return MethodSpec.methodBuilder("reset")
                .addModifiers(Modifier.PUBLIC)
                .addStatement(resetMockStatement)
                .build();
    }
}
