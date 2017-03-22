package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.oroplatform.idea.oroplatform.Functions.toStream;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.elementFilter;

public class YamlPsiElements {

    public static Collection<YAMLKeyValue> getKeyValuesFrom(PsiElement element) {
        return Stream.of(element.getChildren())
            .flatMap(elementFilter(YAMLKeyValue.class))
            .collect(Collectors.toList());
    }

    private static Collection<YAMLKeyValue> getKeyValuesFrom(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(element -> Stream.of(element.getChildren()))
            .flatMap(elementFilter(YAMLKeyValue.class))
            .collect(Collectors.toList());
    }

    static Collection<YAMLScalar> filterScalars(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(elementFilter(YAMLScalar.class))
            .collect(Collectors.toList());
    }

    static Collection<YAMLMapping> filterMappings(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(elementFilter(YAMLMapping.class))
            .collect(Collectors.toList());
    }

    public static Collection<YAMLSequence> filterSequences(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(elementFilter(YAMLSequence.class))
            .collect(Collectors.toList());
    }

    public static List<YAMLMapping> getMappingsFrom(@NotNull PsiFile file) {
        return Stream.of(file.getChildren())
            .flatMap(elementFilter(YAMLDocument.class))
            .flatMap(doc -> Stream.of(doc.getChildren()))
            .flatMap(elementFilter(YAMLMapping.class))
            .collect(Collectors.toList());
    }

    public static List<YAMLMapping> getMappingsFrom(YAMLKeyValue keyValue) {
        return toStream(keyValue::getValue)
            .flatMap(elementFilter(YAMLMapping.class))
            .collect(Collectors.toList());
    }

    public static List<YAMLPsiElement> getSequenceItems(Collection<? extends PsiElement> elements) {
        return elements.stream()
            .flatMap(elementFilter(YAMLSequence.class))
            .flatMap(seq -> seq.getItems().stream())
            .flatMap(item -> toStream(item::getValue))
            .collect(Collectors.toList());
    }

    public static Optional<YAMLKeyValue> getYamlKeyValueSiblingWithName(YAMLKeyValue keyValue, String name) {
        return Stream.of(keyValue.getParent().getChildren())
            .flatMap(elementFilter(YAMLKeyValue.class))
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

    public static List<PsiElement> getOrderedAncestors(PsiElement element) {
        return getAncestors(element, new LinkedList<>());
    }

    private static <Coll extends Collection<PsiElement>> Coll getAncestors(PsiElement element, Coll ancestors) {
        final PsiElement parent = element.getParent();

        if(parent == null || parent instanceof PsiFile) {
            return ancestors;
        }

        ancestors.add(parent);

        return getAncestors(parent, ancestors);
    }

    public static Stream<PhpClass> getFirstPhpClassKeyFromAncestors(PsiElement element) {
        return getFirstPhpClassKeyFromAncestors(element, keyValue -> true);
    }

    public static Stream<PhpClass> getFirstPhpClassKeyFromAncestors(PsiElement element, Predicate<YAMLKeyValue> predicate) {
        return YamlPsiElements.getOrderedAncestors(element).stream()
            .flatMap(elementFilter(YAMLKeyValue.class))
            .filter(predicate)
            .flatMap(keyValue -> Stream.of(keyValue.getReferences()))
            .flatMap(reference -> toStream(reference.resolve()))
            .flatMap(elementFilter(PhpClass.class))
            .limit(1); // get only first class reference from the closest KeyValue
    }

    public static Collection<String> getPropertyFrom(PropertyPath path, PsiElement element) {
        final List<YAMLMapping> elements = getMappingsFrom(element.getContainingFile());
        final Set<PsiElement> ancestors = getAncestors(element);
        return path.doesPointToValue() ? getPropertyValuesFrom(path, elements, ancestors) : getPropertyKeysFrom(path, elements, ancestors);
    }

    public static Collection<String> getPropertyFrom(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        return path.doesPointToValue() ? getPropertyValuesFrom(path, elements, ancestors) : getPropertyKeysFrom(path, elements, ancestors);
    }

    public static Collection<? extends YAMLPsiElement> getElementsByPath(PropertyPath path, PsiElement element) {
        final Collection<YAMLMapping> elements = getMappingsFrom(element.getContainingFile());
        final Set<PsiElement> ancestors = getAncestors(element);

        return path.doesPointToValue() ? getElementsValuesByPath(path, elements, ancestors) : getElementsKeysByPath(path, elements, ancestors);
    }

    public static Optional<String> getTextOfPhpString(PsiElement element) {
        if(element instanceof ClassConstantReference && ((ClassConstantReference) element).resolve() instanceof Field) {
            final Field classConstant = (Field) ((ClassConstantReference) element).resolve();
            return Optional.ofNullable(classConstant.getDefaultValue()).map(PsiElement::getText).map(StringUtil::stripQuotesAroundValue);
        } else if (element instanceof StringLiteralExpression){
            return Optional.of(((StringLiteralExpression) element).getContents());
        } else {
            return Optional.empty();
        }
    }

    private static Collection<? extends YAMLPsiElement> getElementsKeysByPath(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(path.getProperties().isEmpty()) {
            return getParentKeyValuesFrom(elements);
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getElementsKeysByPath(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
    }

    private static Collection<YAMLPsiElement> getElementsValuesByPath(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(path.getProperties().isEmpty()) {
            return getElementsValuesFrom(elements);
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getElementsValuesByPath(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
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
            return getPropertyValuesFrom(elements.stream()
                .filter(element -> path.getCondition().map(condition -> meetsCondition(elements, condition)).orElse(true))
                .collect(Collectors.<YAMLPsiElement>toList()));
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getPropertyValuesFrom(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
    }

    @NotNull
    private static Boolean meetsCondition(Collection<? extends YAMLPsiElement> elements, PropertyPath.Condition condition) {
        final PropertyPath relativePropertyPath = condition.getRelativePropertyPath();
        final String expectedValue = condition.getExpectedValue();
        return getPropertyValuesFrom(relativePropertyPath, elements, Collections.emptySet()).contains(expectedValue);
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
            //TODO: refactor this mess. varargs Stream.concat?
            return Stream.concat(
                filterMappings(elements).stream()
                    .flatMap(mapping -> property.isWildcard() ? mapping.getKeyValues().stream() : toStream(mapping.getKeyValueByKey(property.getName())))
                    .flatMap(keyValue -> toStream(keyValue::getValue)),
                Stream.concat(
                    property.isWildcard() ? getSequenceItems(elements).stream() : filterSequences(elements).stream(),
                    "..".equals(property.getName()) ?
                        elements.stream().flatMap(YamlPsiElements::parentOf).flatMap(YamlPsiElements::parentOf).flatMap(elementFilter(YAMLPsiElement.class)) :
                        Stream.empty()
                )
            ).collect(Collectors.toList());
        }
    }

    private static Collection<String> getParentKeysFrom(Collection<? extends YAMLPsiElement> elements) {
        return getParentKeyValuesFrom(elements).stream()
            .map(YAMLKeyValue::getKeyText)
            .collect(Collectors.toList());
    }

    private static Collection<YAMLKeyValue> getParentKeyValuesFrom(Collection<? extends YAMLPsiElement> elements) {
        return elements.stream()
            .flatMap(element -> toStream(element.getParent()))
            .flatMap(elementFilter(YAMLKeyValue.class))
            .collect(Collectors.toList());
    }

    private static Stream<PsiElement> parentOf(PsiElement element) {
        return toStream(element.getParent());
    }

    private static Collection<String> getPropertyValuesFrom(Collection<? extends YAMLPsiElement> elements) {
        return Stream.concat(
            elements.stream()
                .flatMap(elementFilter(YAMLScalar.class))
                .map(YAMLScalar::getTextValue),
            elements.stream()
                .flatMap(elementFilter(YAMLMapping.class))
                .flatMap(element -> element.getKeyValues().stream())
                .map(YAMLKeyValue::getKeyText)
        ).collect(Collectors.toList());
    }

    private static Collection<YAMLPsiElement> getElementsValuesFrom(Collection<? extends YAMLPsiElement> elements) {
        return Stream.concat(
            elements.stream()
                .flatMap(elementFilter(YAMLScalar.class)),
            elements.stream()
                .flatMap(elementFilter(YAMLMapping.class))
                .flatMap(element -> element.getKeyValues().stream())
        ).collect(Collectors.toList());
    }

}
