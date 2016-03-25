package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import org.jetbrains.yaml.YAMLLanguage;

import static com.intellij.patterns.PlatformPatterns.psiFile;

public class AclCompletion extends CompletionContributor {

    public AclCompletion() {
        Visitor visitor = new CompletionSchemaVisitor(this, psiFile().withName("acl.yml").withLanguage(YAMLLanguage.INSTANCE));
        Schemas.ACL.accept(visitor);
    }

}
