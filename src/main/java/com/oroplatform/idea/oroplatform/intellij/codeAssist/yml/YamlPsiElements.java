package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

public class YamlPsiElements {

    static Collection<YAMLKeyValue> getKeyValuesFrom(PsiElement element) {
        return Stream.of(element.getChildren())
            .flatMap(filterElement(YAMLKeyValue.class))
            .collect(Collectors.toList());
    }

    private static Collection<YAMLKeyValue> getKeyValuesFrom(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(element -> Stream.of(element.getChildren()))
            .flatMap(filterElement(YAMLKeyValue.class))
            .collect(Collectors.toList());
    }

    private static <T extends PsiElement> Function<PsiElement, Stream<T>> filterElement(Class<T> cls) {
        return element -> Stream.of(element)
            .filter(e -> cls.isAssignableFrom(e.getClass()))
            .map(cls::cast);
    }

    static Collection<YAMLScalar> filterScalars(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(filterElement(YAMLScalar.class))
            .collect(Collectors.toList());
    }

    static Collection<YAMLMapping> filterMappings(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(filterElement(YAMLMapping.class))
            .collect(Collectors.toList());
    }

    private static Collection<YAMLSequence> filterSequences(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(filterElement(YAMLSequence.class))
            .collect(Collectors.toList());
    }

    public static List<YAMLMapping> getMappingsFrom(@NotNull PsiFile file) {
        return Stream.of(file.getChildren())
            .flatMap(filterElement(YAMLDocument.class))
            .flatMap(doc -> Stream.of(doc.getChildren()))
            .flatMap(filterElement(YAMLMapping.class))
            .collect(Collectors.toList());
    }

    public static List<YAMLPsiElement> getSequenceItems(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(filterElement(YAMLSequence.class))
            .flatMap(seq -> seq.getItems().stream())
            .flatMap(item -> toStream(item::getValue))
            .collect(Collectors.toList());
    }

    public static Optional<YAMLKeyValue> getYamlKeyValueSiblingWithName(YAMLKeyValue keyValue, String name) {
        return Stream.of(keyValue.getParent().getChildren())
            .flatMap(filterElement(YAMLKeyValue.class))
            .filter(element -> name.equals(element.getKeyText()))
            .findFirst();
    }

    public static Optional<YAMLMapping> getFirstMapping(PsiElement element) {
        return getFirstMapping(element, Integer.MAX_VALUE);
    }

    public static Optional<YAMLMapping> getFirstMapping(PsiElement element, int maxDepth) {
        if(maxDepth == 0) return Optional.empty();

        if(element instanceof YAMLMapping) {
            return Optional.of((YAMLMapping) element);
        }

        return element == null ? Optional.empty() : getFirstMapping(element.getParent(), --maxDepth);
    }

    public static Set<PsiElement> getAncestors(PsiElement element) {
        return getAncestors(element, new HashSet<>());
    }

    private static Set<PsiElement> getAncestors(PsiElement element, Set<PsiElement> ancestors) {
        final PsiElement parent = element.getParent();

        if(parent == null || parent instanceof PsiFile) {
            return ancestors;
        }

        ancestors.add(parent);

        return getAncestors(parent, ancestors);
    }

    public static Collection<String> getPropertyFrom(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        return path.doesPointToValue() ? getPropertyValuesFrom(path, elements, ancestors) : getPropertyKeysFrom(path, elements, ancestors);
    }

    private static Collection<String> getPropertyKeysFrom(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(path.getProperties().isEmpty()) {
            return getParentKeysFrom(elements);
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getPropertyKeysFrom(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
    }

    private static Collection<String> getPropertyValuesFrom(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(path.getProperties().isEmpty()) {
            return getPropertyValuesFrom(elements);
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getPropertyValuesFrom(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
    }

    @NotNull
    private static Collection<YAMLPsiElement> getElementsForProperty(PropertyPath.Property property, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(property.isThis()) {
            return Stream.concat(
                getKeyValuesFrom(elements).stream()
                    .filter(keyValue -> keyValue.getValue() != null && ancestors.contains(keyValue.getValue()))
                    .map(YAMLKeyValue::getValue),
                getSequenceItems(elements).stream()
                    .filter(ancestors::contains)
            ).collect(Collectors.toList());
        } else {
            return Stream.concat(
                filterMappings(elements).stream()
                    .flatMap(mapping -> toStream(() -> mapping.getKeyValueByKey(property.getName())))
                    .flatMap(keyValue -> toStream(keyValue::getValue)),
                filterSequences(elements).stream()
            ).collect(Collectors.toList());
        }
    }

    private static Collection<String> getParentKeysFrom(Collection<? extends YAMLPsiElement> elements) {
        return elements.stream()
            .flatMap(element -> toStream(() -> element.getParent()))
            .flatMap(filterElement(YAMLKeyValue.class))
            .map(YAMLKeyValue::getKeyText)
            .collect(Collectors.toList());
    }

    private static Collection<String> getPropertyValuesFrom(Collection<? extends YAMLPsiElement> elements) {
        return Stream.concat(
            elements.stream()
                .flatMap(filterElement(YAMLScalar.class))
                .map(YAMLScalar::getTextValue),
            elements.stream()
                .flatMap(filterElement(YAMLMapping.class))
                .flatMap(element -> element.getKeyValues().stream())
                .map(YAMLKeyValue::getKeyText)
        ).collect(Collectors.toList());
    }

}
