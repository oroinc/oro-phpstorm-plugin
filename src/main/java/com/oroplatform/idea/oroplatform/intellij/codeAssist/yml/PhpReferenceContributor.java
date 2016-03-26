package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLDocument;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;

public class PhpReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        Visitor visitor = new PhpReferenceVisitor(registrar, psiElement(YAMLDocument.class).inFile(psiFile().withName("acl.yml").withLanguage(YAMLLanguage.INSTANCE)));
        Schemas.ACL.accept(visitor);
    }
}
