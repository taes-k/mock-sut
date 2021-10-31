package io.github.taesk;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.github.taesk.parser.ParserFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@AutoService(Processor.class)
public class MockSutProcessor extends AbstractProcessor {

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
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "MockSut have to annotated on class");
            } else {
                generateCode((TypeElement) element);
            }
        }

        return true;

    }

    private void generateCode(TypeElement element) {
        var className = ClassName.get(element).simpleName();
        var packageName = ClassName.get(element).packageName();
        var generateClassName = String.format("%sSutFactory", className);

        ParserFactory parserFactory = new ParserFactory(element);
        var fieldSpecs = parserFactory.getFieldSpecs();
        var constructorSpec = parserFactory.getConstructorSpec();
        var getterMethodSpecs = parserFactory.getGetterMethodSpecs();

        var classSpec = TypeSpec.classBuilder(generateClassName)
                .addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpecs)
                .addMethod(constructorSpec)
                .addMethods(getterMethodSpecs)
                .build();

        generateFile(packageName, classSpec);
    }

    private void generateFile(String packageName, TypeSpec classSpec) {
        try {
            JavaFile.builder(packageName, classSpec)
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            var message = String.format("Generate %s Failed", classSpec.name);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);

            throw new RuntimeException(e);
        }
    }
}
