package io.github.taesk.parser.field;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import io.github.taesk.parser.Parser;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MockFieldParser implements Parser<List<FieldSpec>> {
    private static final String LOMBOK_REQUIRED_ARGUMENTS_CONSTRUCTOR = "RequiredArgsConstructor";
    private static final String LOMBOK_ALL_ARGUMENTS_CONSTRUCTOR = "AllArgsConstructor";

    private final TypeElement element;
    private final Trees trees;

    public MockFieldParser(TypeElement element, Trees trees) {
        this.element = element;
        this.trees = trees;
    }

    public List<FieldSpec> invoke() {
        List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
        List<String> annotationNames = annotations.stream()
                .map(it -> it.getAnnotationType().asElement().getSimpleName().toString())
                .collect(Collectors.toList());

        if (annotationNames.contains(LOMBOK_ALL_ARGUMENTS_CONSTRUCTOR)) {
            return getAllFieldSpecs();
        }

        if (annotationNames.contains(LOMBOK_REQUIRED_ARGUMENTS_CONSTRUCTOR)) {
            return getRequiredFieldSpecs();
        }

        return getConstructorFieldSpecs();
    }

    private List<FieldSpec> getConstructorFieldSpecs() {
        long constructorCount = element.getEnclosedElements().stream()
                .filter(it -> it.getKind() == ElementKind.CONSTRUCTOR && it.getModifiers().contains(Modifier.PUBLIC))
                .count();

        if (constructorCount < 1) {
            return Collections.emptyList();
        }

        ExecutableElement constructorElement;
        if (constructorCount == 1) {
            constructorElement = (ExecutableElement) element.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.CONSTRUCTOR && it.getModifiers().contains(Modifier.PUBLIC))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Constructor parse error"));
        } else {
            constructorElement = (ExecutableElement) element.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.CONSTRUCTOR && it.getModifiers().contains(Modifier.PUBLIC))
                    .max(Comparator.comparing(it -> ((ExecutableElement) it).getParameters().size()))
                    .orElseThrow(() -> new RuntimeException("Constructor parse error"));
        }

        return getFieldSpecs(constructorElement);
    }

    private List<FieldSpec> getAllFieldSpecs() {
        return element.getEnclosedElements().stream()
                .filter(it -> it.getKind().isField())
                .map(it -> {
                            String paramName = it.getSimpleName().toString();
                            TypeName paramType = TypeName.get(it.asType());

                            return FieldSpec.builder(paramType, paramName)
                                    .addModifiers(Modifier.PRIVATE)
                                    .build();
                        }
                ).collect(Collectors.toList());
    }

    private List<FieldSpec> getRequiredFieldSpecs() {
        return element.getEnclosedElements().stream()
                .filter(it -> it.getKind().isField())
                .filter(it -> !it.getModifiers().contains(Modifier.STATIC))
                .filter(it -> it.getModifiers().contains(Modifier.FINAL) || it.getAnnotation(Nonnull.class) != null)
                .filter(it -> !it.getSimpleName().toString().startsWith("$"))
                .filter(it -> ((JCTree.JCVariableDecl) trees.getTree(it)).init == null) // 초기화 된 필드 제거
                .map(it -> {
                            String paramName = it.getSimpleName().toString();
                            TypeName paramType = TypeName.get(it.asType());

                            return FieldSpec.builder(paramType, paramName)
                                    .addModifiers(Modifier.PRIVATE)
                                    .build();
                        }
                ).collect(Collectors.toList());
    }

    private List<FieldSpec> getFieldSpecs(ExecutableElement constructorElement) {
        return constructorElement.getParameters().stream()
                .map(it -> {
                            String paramName = it.getSimpleName().toString();
                            TypeName paramType = TypeName.get(it.asType());

                            return FieldSpec.builder(paramType, paramName)
                                    .addModifiers(Modifier.PRIVATE)
                                    .build();
                        }
                ).collect(Collectors.toList());
    }
}
