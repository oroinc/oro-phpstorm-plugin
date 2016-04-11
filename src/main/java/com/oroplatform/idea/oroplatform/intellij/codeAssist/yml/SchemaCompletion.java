package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.oroplatform.idea.oroplatform.schema.Schema;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YmlPatterns.getDocumentPattern;

public class SchemaCompletion extends CompletionContributor {

    public SchemaCompletion() {
        for(Schema schema : Schemas.ALL) {
            Visitor visitor = new CompletionSchemaVisitor(this, getDocumentPattern(schema.fileName), YmlVisitor.VisitingContext.PROPERTY_VALUE);
            schema.rootElement.accept(visitor);
        }
    }

}
