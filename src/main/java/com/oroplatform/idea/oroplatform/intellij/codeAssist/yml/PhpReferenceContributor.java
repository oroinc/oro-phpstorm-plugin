package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.oroplatform.idea.oroplatform.schema.Schema;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import org.jetbrains.annotations.NotNull;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YmlPatterns.getDocumentPattern;

public class PhpReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        for(Schema schema : Schemas.ALL) {
            Visitor visitor = new PhpReferenceVisitor(registrar, getDocumentPattern(schema.fileName), YmlVisitor.VisitingContext.PROPERTY_VALUE);
            schema.rootElement.accept(visitor);
        }
    }

}
