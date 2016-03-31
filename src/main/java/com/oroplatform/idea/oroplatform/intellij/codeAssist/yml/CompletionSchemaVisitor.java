package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ChoiceCompletionProvider;
import com.oroplatform.idea.oroplatform.schema.Container;
import com.oroplatform.idea.oroplatform.schema.Scalar;
import com.oroplatform.idea.oroplatform.schema.Property;
import com.oroplatform.idea.oroplatform.schema.Visitor;

import java.util.LinkedList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

class CompletionSchemaVisitor extends YmlVisitor {
    private final CompletionContributor completion;

    CompletionSchemaVisitor(CompletionContributor completion, ElementPattern<? extends PsiElement> capture) {
        super(capture);
        this.completion = completion;
    }

    @Override
    protected Visitor nextVisitor(ElementPattern<? extends PsiElement> capture) {
        return new CompletionSchemaVisitor(completion, capture);
    }

    @Override
    protected void handleContainer(Container container, ElementPattern<? extends PsiElement> captureElement) {
        List<String> properties = new LinkedList<String>();

        for(Property property : container.getProperties()) {
            properties.addAll(property.nameExamples());
        }
        completion.extend(
            CompletionType.BASIC,
            captureElement,
            new ChoiceCompletionProvider(properties)
        );
    }

    @Override
    public void visitLiteralChoicesValue(Scalar.Choices choices) {
        completion.extend(
            CompletionType.BASIC,
            psiElement(LeafPsiElement.class).withSuperParent(2, capture),
            new ChoiceCompletionProvider(choices.getChoices())
        );
    }
}
