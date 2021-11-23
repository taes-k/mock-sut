package io.github.taesk;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.util.Trees;
import io.github.taesk.parser.ParserFactory;

@SuppressWarnings("unused")
@AutoService(Processor.class)
public class MockSutProcessor extends AbstractProcessor {
    static final String SUFFIX_CLASS_NAME = "MockSutFactory";
    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
    }

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

        for (Element element : elements) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "MockSut have to annotated on class");
            } else {
                generateCode((TypeElement)element);
            }
        }

        return true;

    }

    private void generateCode(TypeElement element) {
        String className = ClassName.get(element).simpleName();
        String packageName = ClassName.get(element).packageName();
        String generateClassName = String.format("%s" + SUFFIX_CLASS_NAME, className);

        ParserFactory parserFactory = new ParserFactory(element, trees, className, generateClassName);
        List<FieldSpec> fieldSpecs = parserFactory.getFieldSpecs();
        MethodSpec constructorSpec = parserFactory.getConstructorSpec();
        List<MethodSpec> getterMethodSpecs = parserFactory.getGetterMethodSpecs();
        List<MethodSpec> setSpyMethodSpecs = parserFactory.getSetSpyMethodSpecs();
        MethodSpec resetMethodSpec = parserFactory.getResetMethodSpecs();

        TypeSpec classSpec = TypeSpec.classBuilder(generateClassName)
            .addModifiers(Modifier.PUBLIC)
            .addFields(fieldSpecs)
            .addMethod(constructorSpec)
            .addMethods(getterMethodSpecs)
            .addMethods(setSpyMethodSpecs)
            .addMethod(resetMethodSpec)
            .build();

        generateFile(packageName, classSpec);
    }

    @SuppressWarnings("java:S112")
    private void generateFile(String packageName, TypeSpec classSpec) {
        try {
            JavaFile.builder(packageName, classSpec)
                .build()
                .writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            String message = String.format("Generate %s Failed", classSpec.name);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);

            throw new RuntimeException(e);
        }
    }
}
