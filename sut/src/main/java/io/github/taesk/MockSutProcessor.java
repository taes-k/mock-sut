package io.github.taesk;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.github.taesk.parser.MockFieldParser;
import io.github.taesk.parser.SutFieldParser;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@AutoService(Processor.class)
public class MockSutProcessor extends AbstractProcessor {
    private final MockFieldParser mockFieldParser = new MockFieldParser();
    private final SutFieldParser sutFieldParser = new SutFieldParser();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(MockSut.class.getName());

        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(MockSut.class);

        for (var element : elements) {
            var typeElement = (TypeElement) element;
            var parentClassName = ClassName.get(typeElement).simpleName();
            var packageName = ClassName.get(typeElement).packageName();
            var className = String.format("%sSutFactory", parentClassName);

            var mockFieldSpecs = mockFieldParser.invoke(typeElement);
            var sutFieldSpec = sutFieldParser.invoke(typeElement);

            var mockFieldInitStatement = mockFieldSpecs.stream()
                    .map(it -> CodeBlock.of("this.$N = $T.mock($T.class);", it, Mockito.class, it.type))
                    .reduce((a, b) -> CodeBlock.join(List.of(a, b), "\n"))
                    .orElseGet(() -> CodeBlock.of(""));

            var sutFieldInitStatement =
                    CodeBlock.builder()
                            .add("this.sut = new $T(", ClassName.get(typeElement))
                            .add(mockFieldSpecs.stream()
                                    .map(it -> CodeBlock.of("$N", it))
                                    .reduce((a, b) -> CodeBlock.join(List.of(a, b), ", "))
                                    .orElseGet(() -> CodeBlock.of(""))
                            )
                            .add(")")
                            .build();

            var constructorSpec = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement(CodeBlock.join(List.of(mockFieldInitStatement, sutFieldInitStatement), "\n"))
                    .build();


            var getterMethodSpec = ListUtils.union(mockFieldSpecs, List.of(sutFieldSpec)).stream()
                    .map(it -> MethodSpec.methodBuilder(String.format("get%s", StringUtils.capitalize(it.name)))
                            .addModifiers(Modifier.PUBLIC)
                            .returns(it.type)
                            .addStatement(String.format("return this.%s", it.name))
                            .build())
                    .collect(Collectors.toList());

            var classSpec = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addFields(mockFieldSpecs)
                    .addField(sutFieldSpec)
                    .addMethod(constructorSpec)
                    .addMethods(getterMethodSpec)
                    .build();

            try {
                JavaFile.builder(packageName, classSpec)
                        .build()
                        .writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;

    }
}
