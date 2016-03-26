package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLDocument;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;

public class AclCompletion extends CompletionContributor {

    public AclCompletion() {
        Visitor visitor = new CompletionSchemaVisitor(this, psiElement(YAMLDocument.class).inFile(psiFile().withName("acl.yml").withLanguage(YAMLLanguage.INSTANCE)));
        Schemas.ACL.accept(visitor);
    }

}
