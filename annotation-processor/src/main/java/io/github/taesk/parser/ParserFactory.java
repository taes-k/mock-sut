package io.github.taesk.parser;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.TypeElement;

import org.apache.commons.collections4.ListUtils;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.util.Trees;
import io.github.taesk.parser.clazz.BuilderClassParser;
import io.github.taesk.parser.constructor.AllParameterConstructorParser;
import io.github.taesk.parser.constructor.NonParameterConstructorParser;
import io.github.taesk.parser.field.MockFieldParser;
import io.github.taesk.parser.field.SutFieldParser;
import io.github.taesk.parser.method.BuilderMethodParser;
import io.github.taesk.parser.method.GetterMethodParser;
import io.github.taesk.parser.method.ResetMethodParser;

public class ParserFactory {
    private final MockFieldParser mockFieldParser;
    private final SutFieldParser sutFieldParser;
    private final NonParameterConstructorParser nonParameterConstructorParser;
    private final AllParameterConstructorParser allParameterConstructorParser;
    private final BuilderClassParser builderClassParser;
    private final BuilderMethodParser builderMethodParser;

    public ParserFactory(TypeElement element, Trees trees, String originClassName, String generateClassName) {
        mockFieldParser = new MockFieldParser(element, trees);
        sutFieldParser = new SutFieldParser(element);
        nonParameterConstructorParser = new NonParameterConstructorParser(element, mockFieldParser);
        allParameterConstructorParser = new AllParameterConstructorParser(element, mockFieldParser);
        builderClassParser = new BuilderClassParser(element, mockFieldParser, generateClassName);
        builderMethodParser = new BuilderMethodParser(generateClassName);
    }

    public List<FieldSpec> getFieldSpecs() {
        List<FieldSpec> mockFields = getMockFieldSpecs();
        FieldSpec sutFields = getSutFieldSpec();

        return ListUtils.union(mockFields, Collections.singletonList(sutFields));
    }

    public TypeSpec getBuilderClassType() {
        return builderClassParser.invoke();
    }

    private List<FieldSpec> getMockFieldSpecs() {
        return mockFieldParser.invoke();
    }

    private FieldSpec getSutFieldSpec() {
        return sutFieldParser.invoke();
    }

    public List<MethodSpec> getConstructorSpecs() {
        return List.of(nonParameterConstructorParser.invoke(), allParameterConstructorParser.invoke());
    }

    public List<MethodSpec> getGetterMethodSpecs() {
        return new GetterMethodParser(getFieldSpecs()).invoke();
    }

    public MethodSpec getResetMethodSpecs() {
        return new ResetMethodParser(getMockFieldSpecs()).invoke();
    }

    public MethodSpec getBuilderMethodSpec() {
        return builderMethodParser.invoke();
    }

}
