package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;

class YmlPatterns {

    static ElementPattern<? extends PsiElement> key(ElementPattern<? extends PsiElement> parent) {
        return psiElement().andOr(
            //some:
            //  <caret>: ~
            psiElement(LeafPsiElement.class).withSuperParent(2, parent),
            //for references
            //some:
            //  <caret>: ~
            psiElement(YAMLKeyValue.class).withParent(parent)
        );
    }

    static ElementPattern<? extends PsiElement> keyInProgress(ElementPattern<? extends PsiElement> superParent, ElementPattern<? extends PsiElement> parent) {
        return psiElement().andOr(
            //some:
            //  <caret>
            psiElement(LeafPsiElement.class).withSuperParent(2, superParent),
            //for references
            //some:
            //  xxx: ~
            //  <caret>
            psiElement(YAMLScalar.class).withSuperParent(2, superParent),
            //some:
            //  <caret>
            psiElement(YAMLScalar.class).withParent(superParent),
            key(parent)
        );
    }

    static ElementPattern<? extends PsiElement> sequence(ElementPattern<? extends PsiElement> parent) {
        return psiElement(YAMLSequenceItem.class).withSuperParent(2, parent);
    }

    static ElementPattern<? extends PsiElement> mapping(ElementPattern<? extends PsiElement> parent) {
        return psiElement(YAMLMapping.class).withParent(parent);
    }

    static PsiElementPattern.Capture<YAMLDocument> getDocumentPattern(String fileName) {
        return psiElement(YAMLDocument.class).inFile(psiFile().withName(fileName).withLanguage(YAMLLanguage.INSTANCE));
    }
}
