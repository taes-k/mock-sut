package io.github.taesk.parser;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.collections4.ListUtils;

import javax.lang.model.element.TypeElement;
import java.util.List;

public class ParserFactory {
    private final MockFieldParser mockFieldParser;
    private final SutFieldParser sutFieldParser;
    private final ConstructorParser constructorParser;

    public ParserFactory(TypeElement element) {
        mockFieldParser = new MockFieldParser(element);
        sutFieldParser = new SutFieldParser(element);
        constructorParser = new ConstructorParser(element, mockFieldParser);
    }

    public List<FieldSpec> getFieldSpecs() {
        var mockFields = mockFieldParser.invoke();
        var sutFields = sutFieldParser.invoke();

        return ListUtils.union(mockFields, List.of(sutFields));
    }

    public MethodSpec getConstructorSpec() {
        return constructorParser.invoke();
    }

    public List<MethodSpec> getGetterMethodSpecs() {
        return new GetterMethodParser(getFieldSpecs()).invoke();
    }
}
