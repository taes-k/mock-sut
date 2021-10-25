//package io.github.taesk
//
//import com.google.auto.service.AutoService
//import com.squareup.javapoet.ClassName
//import com.squareup.javapoet.FieldSpec
//import com.squareup.javapoet.JavaFile
//import com.squareup.javapoet.MethodSpec
//import com.squareup.javapoet.TypeSpec
//import io.github.taesk.parser.ConstructorParser
//import javax.annotation.processing.AbstractProcessor
//import javax.annotation.processing.Processor
//import javax.annotation.processing.RoundEnvironment
//import javax.lang.model.SourceVersion
//import javax.lang.model.element.Element
//import javax.lang.model.element.Modifier
//import javax.lang.model.element.TypeElement
//import javax.tools.Diagnostic
//
//
//@AutoService(Processor::class)
//public class MockSutProcessorKt : AbstractProcessor() {
//
//    override fun getSupportedAnnotationTypes(): Set<String>? {
//        // 타겟 annotation class 정의
//        val set: MutableSet<String> = HashSet()
//        set.add(MockSut::class.java.getName())
//        return set
//    }
//
//    override fun getSupportedSourceVersion(): SourceVersion? {
//        return SourceVersion.latestSupported()
//    }
//
//    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
//        val elements: Set<Element?> = roundEnv.getElementsAnnotatedWith(MockSut::class.java)
//
//        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "CustomGetter have to annotated on class");
//        if (elements.isEmpty())
//            return false
//
//        for (element in elements) {
//            val typeElement = element as TypeElement
//            val parentClassName = ClassName.get(typeElement).simpleName()
//            val packageName = ClassName.get(typeElement).packageName()
//            val className = "${parentClassName}MockSut"
//
//            val constructorFieldSpecs = ConstructorParser(typeElement)
//
//            val classSpec = TypeSpec.classBuilder(className)
//                .addModifiers(Modifier.PUBLIC)
//                .build()
//
//            JavaFile.builder(packageName, classSpec)
//                .build()
//                .writeTo(processingEnv.filer)
//
//        }
//
//        return true
//    }
//}