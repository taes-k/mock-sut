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
import io.github.taesk.parser.constructor.ConstructorParser;
import io.github.taesk.parser.field.MockFieldParser;
import io.github.taesk.parser.field.SutFieldParser;
import io.github.taesk.parser.method.GetterMethodParser;
import io.github.taesk.parser.method.ResetMethodParser;
import io.github.taesk.parser.method.WithSpyMethodParser;

public class ParserFactory {
    private final String originClassName;
    private final String generateClassName;
    private final BuilderClassParser builderClassParser;
    private final MockFieldParser mockFieldParser;
    private final SutFieldParser sutFieldParser;
    private final ConstructorParser constructorParser;

    public ParserFactory(TypeElement element, Trees trees, String originClassName, String generateClassName) {
        this.originClassName = originClassName;
        this.generateClassName = generateClassName;

        mockFieldParser = new MockFieldParser(element, trees);
        sutFieldParser = new SutFieldParser(element);
        constructorParser = new ConstructorParser(element, mockFieldParser);
        builderClassParser = new BuilderClassParser(element, mockFieldParser, generateClassName);
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

    public MethodSpec getConstructorSpec() {
        return constructorParser.invoke();
    }

    public List<MethodSpec> getGetterMethodSpecs() {
        return new GetterMethodParser(getFieldSpecs()).invoke();
    }

    public MethodSpec getResetMethodSpecs() {
        return new ResetMethodParser(getMockFieldSpecs()).invoke();
    }

    public List<MethodSpec> getSetSpyMethodSpecs() {
        return new WithSpyMethodParser(originClassName, generateClassName, getMockFieldSpecs()).invoke();
    }

}
