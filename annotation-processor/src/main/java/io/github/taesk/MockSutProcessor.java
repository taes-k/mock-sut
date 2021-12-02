package io.github.taesk;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
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
        ProcessingEnvironment env = unwrap(processingEnv);
        this.trees = Trees.instance(env);
    }

    private static ProcessingEnvironment unwrap(ProcessingEnvironment processingEnv) {
        if (Proxy.isProxyClass(processingEnv.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(processingEnv);
            try {
                Field field = invocationHandler.getClass().getDeclaredField("val$delegateTo");
                field.setAccessible(true);
                Object o = field.get(invocationHandler);
                if (o instanceof ProcessingEnvironment) {
                    return (ProcessingEnvironment)o;
                } else {
                    processingEnv.getMessager()
                        .printMessage(Diagnostic.Kind.ERROR,
                            "got " + o.getClass() + " expected instanceof com.sun.tools.javac.processing.JavacProcessingEnvironment");
                    return null;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                return null;
            }
        } else {
            return processingEnv;
        }
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
                try {
                    generateCode((TypeElement)element);
                } catch (Exception e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, Arrays.toString(e.getStackTrace()));
                }
            }
        }

        return true;

    }

    private void generateCode(TypeElement element) {
        String className = ClassName.get(element).simpleName();
        String packageName = ClassName.get(element).packageName();
        String generateClassName = String.format("%s" + SUFFIX_CLASS_NAME, className);

        ParserFactory parserFactory = new ParserFactory(element, trees, className, generateClassName);
        TypeSpec builderClass = parserFactory.getBuilderClassType();

        List<FieldSpec> fieldSpecs = parserFactory.getFieldSpecs();
        List<MethodSpec> constructorSpecs = parserFactory.getConstructorSpecs();
        List<MethodSpec> getterMethodSpecs = parserFactory.getGetterMethodSpecs();
        MethodSpec resetMethodSpec = parserFactory.getResetMethodSpecs();
        MethodSpec builderMethodSpec = parserFactory.getBuilderMethodSpec();

        TypeSpec classSpec = TypeSpec.classBuilder(generateClassName)
            .addModifiers(Modifier.PUBLIC)
            .addType(builderClass)
            .addFields(fieldSpecs)
            .addMethods(constructorSpecs)
            .addMethods(getterMethodSpecs)
            .addMethod(resetMethodSpec)
            .addMethod(builderMethodSpec)
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
