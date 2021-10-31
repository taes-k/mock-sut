package io.github.taesk.parser;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Collectors;

public class GetterMethodParser implements Parser<List<MethodSpec>> {
    private final List<FieldSpec> fieldSpecs;

    public GetterMethodParser(List<FieldSpec> fieldSpecs) {
        this.fieldSpecs = fieldSpecs;
    }

    public List<MethodSpec> invoke() {
        return fieldSpecs.stream()
                .map(it -> MethodSpec.methodBuilder(String.format("get%s", StringUtils.capitalize(it.name)))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(it.type)
                        .addStatement(String.format("return this.%s", it.name))
                        .build())
                .collect(Collectors.toList());
    }
}
