package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLSequenceItem;

import static com.intellij.patterns.PlatformPatterns.psiElement;

class YmlPatterns {

    static ElementPattern<? extends PsiElement> key(ElementPattern<? extends PsiElement> parent) {
        return psiElement(LeafPsiElement.class).withSuperParent(2, parent);
    }

    static ElementPattern<? extends PsiElement> keyInProgress(ElementPattern<? extends PsiElement> superParent, ElementPattern<? extends PsiElement> parent) {
        return psiElement().andOr(
            psiElement(LeafPsiElement.class).withSuperParent(2, superParent),
            key(parent)
        );
    }

    static ElementPattern<? extends PsiElement> sequence(ElementPattern<? extends PsiElement> parent) {
        return psiElement(YAMLSequenceItem.class).withSuperParent(2, parent);
    }

    static ElementPattern<? extends PsiElement> mapping(ElementPattern<? extends PsiElement> parent) {
        return psiElement(YAMLMapping.class).withParent(parent);
    }
}
