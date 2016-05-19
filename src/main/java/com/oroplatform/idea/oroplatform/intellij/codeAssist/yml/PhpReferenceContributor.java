package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.schema.Schema;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLDocument;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YmlPatterns.getDocumentPattern;

public class PhpReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        for(Schema schema : Schemas.ALL) {
            final PsiElementPattern.Capture<YAMLDocument> documentPattern = getDocumentPattern(schema.fileName).inFile(fileInProjectWithPluginEnabled());
            Visitor visitor = new PhpReferenceVisitor(registrar, documentPattern, YmlVisitor.VisitingContext.PROPERTY_VALUE);
            schema.rootElement.accept(visitor);
        }
    }

    @NotNull
    private PsiElementPattern.Capture<PsiFile> fileInProjectWithPluginEnabled() {
        return psiElement(PsiFile.class).with(new PatternCondition<PsiFile>(null) {
            @Override
            public boolean accepts(@NotNull PsiFile psiFile, ProcessingContext context) {
                return OroPlatformSettings.getInstance(psiFile.getProject()).isPluginEnabled();
            }
        });
    }

}
