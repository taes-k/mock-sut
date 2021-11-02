package io.github.taesk.parser;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import io.github.taesk.parser.constructor.ConstructorParser;
import io.github.taesk.parser.field.MockFieldParser;
import io.github.taesk.parser.field.SutFieldParser;
import io.github.taesk.parser.method.GetterMethodParser;
import io.github.taesk.parser.method.ResetMethodParser;

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
        var mockFields = getMockFieldSpecs();
        var sutFields = getSutFieldSpec();

        return ListUtils.union(mockFields, List.of(sutFields));
    }

    private List<FieldSpec> getMockFieldSpecs(){
        return mockFieldParser.invoke();
    }

    private FieldSpec getSutFieldSpec(){
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
}
