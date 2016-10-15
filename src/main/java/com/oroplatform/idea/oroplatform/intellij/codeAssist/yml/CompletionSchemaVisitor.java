package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ChoicesProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.StaticChoicesProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider.ChoiceCompletionProvider;
import com.oroplatform.idea.oroplatform.schema.*;

import java.util.LinkedList;
import java.util.List;

class CompletionSchemaVisitor extends YamlVisitor {
    private final CompletionProviders completionProviders;
    private final CompletionContributor completion;
    private final PropertyDescriptionProvider propertyDescriptionProvider = new TypePropertyDescriptionProvider();

    CompletionSchemaVisitor(CompletionProviders completionProviders, CompletionContributor completion, ElementPattern<? extends PsiElement> capture, VisitingContext context) {
        super(capture, context);
        this.completionProviders = completionProviders;
        this.completion = completion;
    }

    @Override
    protected Visitor nextVisitor(ElementPattern<? extends PsiElement> capture, VisitingContext context) {
        return new CompletionSchemaVisitor(completionProviders, completion, capture, context);
    }

    @Override
    protected void handleContainer(Container container, ElementPattern<? extends PsiElement> captureElement) {
        List<ChoicesProvider.Choice> choices = new LinkedList<ChoicesProvider.Choice>();

        for(Property property : container.getProperties()) {
            for (String name : property.nameExamples()) {
                choices.add(new ChoicesProvider.Choice(name, propertyDescriptionProvider.getDescription(property)));
            }
        }

        completion.extend(
            CompletionType.BASIC,
            captureElement,
            new ChoiceCompletionProvider(new StaticChoicesProvider(choices), KeyInsertHandler.INSTANCE)
        );
    }

    @Override
    public void visitOneOf(OneOf oneOf) {
        for(Element element : oneOf.getElements()) {
            element.accept(this);
        }
    }

    @Override
    public void visitScalar(Scalar scalar) {
        final CompletionProvider<CompletionParameters> provider = scalar.getProvider(completionProviders, insertHandler);

        if(provider != null) {
            completion.extend(
                CompletionType.BASIC,
                context == VisitingContext.PROPERTY_VALUE ? YamlPatterns.scalarValue().withSuperParent(2, capture) : capture,
                provider
            );
        }
    }
}
