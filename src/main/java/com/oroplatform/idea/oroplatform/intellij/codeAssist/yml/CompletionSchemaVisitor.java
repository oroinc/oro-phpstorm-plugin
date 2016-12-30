package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ChoicesProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.StaticChoicesProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider.ChoiceCompletionProvider;
import com.oroplatform.idea.oroplatform.schema.*;

import java.util.List;
import java.util.stream.Collectors;

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
        final List<ChoicesProvider.Choice> choices = container.getProperties().stream()
            .flatMap(property -> property.nameExamples().stream()
                .map(name -> new ChoicesProvider.Choice(name, propertyDescriptionProvider.getDescription(property), property.getKeyElement().getDefaultValueDescriptor(), property.isDeprecated()))
            )
            .collect(Collectors.toList());

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
        scalar.getProvider(completionProviders, insertHandler).ifPresent(provider -> {
            completion.extend(
                CompletionType.BASIC,
                context == VisitingContext.PROPERTY_VALUE ? YamlPatterns.scalarValue().withSuperParent(2, capture) : capture,
                provider
            );
        });
    }
}
