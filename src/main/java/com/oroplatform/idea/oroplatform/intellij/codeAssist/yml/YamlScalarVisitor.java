package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.elementFilter;

public class YamlScalarVisitor extends PsiRecursiveElementWalkingVisitor {
    private final BiFunction<String, YAMLScalar, Void> consumer;

    public YamlScalarVisitor(BiFunction<String, YAMLScalar, Void> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void visitElement(PsiElement element) {
        if(element instanceof YAMLScalar) {
            final List<String> ancestors = YamlPsiElements.getOrderedAncestors(element).stream()
                .flatMap(elementFilter(YAMLKeyValue.class))
                .map(YAMLKeyValue::getKeyText)
                .collect(Collectors.toList());
            Collections.reverse(ancestors);
            final String ancestorsPath = ancestors.stream().collect(Collectors.joining("."));

            consumer.apply(ancestorsPath, (YAMLScalar) element);
        } else {
            super.visitElement(element);
        }
    }
}
