package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.oroplatform.idea.oroplatform.schema.Schema;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YmlPatterns.getDocumentPattern;

public class SchemaCompletion extends CompletionContributor {

    public SchemaCompletion() {
        for(Schema schema : Schemas.ALL) {
            Visitor visitor = new CompletionSchemaVisitor(this, getDocumentPattern(schema.fileName), YmlVisitor.VisitingContext.PROPERTY_VALUE);
            schema.rootElement.accept(visitor);
        }
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if(OroPlatformSettings.getInstance(parameters.getOriginalFile().getProject()).isPluginEnabled()) {
            super.fillCompletionVariants(parameters, result);
        }
    }
}
