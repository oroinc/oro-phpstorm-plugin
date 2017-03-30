package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider.TranslationReferenceProvider;
import com.oroplatform.idea.oroplatform.schema.Schema;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLDocument;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.fileInProjectWithPluginEnabled;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPatterns.getDocumentPattern;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPatterns.getFilePattern;

public class ReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        for(Schema schema : Schemas.ALL) {
            final PsiElementPattern.Capture<YAMLDocument> documentPattern = getDocumentPattern(schema.fileMatcher).inFile(fileInProjectWithPluginEnabled());

            final Visitor[] visitors = {
                new ReferenceVisitor(new YamlReferenceProviders(), registrar, documentPattern, YamlVisitor.VisitingContext.PROPERTY_VALUE)
            };

            for (Visitor visitor : visitors) {
                schema.rootElement.accept(visitor);
            }

        }

        registerGlobalReferenceProviders(registrar);
    }

    private void registerGlobalReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        final PsiElementPattern.Capture<? extends PsiElement> transPattern = YamlPatterns.scalarValue().withParent(
            psiElement().withName("description", "label", "title", "message", "placeholder")
                .inFile(getFilePattern(Schemas.FilePathMatchers.ORO_CONFIG_FILES)
                    .and(fileInProjectWithPluginEnabled()))
        );
        registrar.registerReferenceProvider(transPattern, new TranslationReferenceProvider(FixMissingQuoteInsertHandler.INSTANCE));
    }

}
