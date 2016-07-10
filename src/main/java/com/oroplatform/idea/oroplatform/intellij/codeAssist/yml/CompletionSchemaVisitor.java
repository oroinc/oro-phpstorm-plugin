package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.ChoiceCompletionProvider.Choice;
import com.oroplatform.idea.oroplatform.schema.*;

import java.util.LinkedList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

class CompletionSchemaVisitor extends YamlVisitor {
    private final CompletionContributor completion;
    private final PropertyDescriptionProvider propertyDescriptionProvider = new TypePropertyDescriptionProvider();

    CompletionSchemaVisitor(CompletionContributor completion, ElementPattern<? extends PsiElement> capture, VisitingContext context) {
        super(capture, context);
        this.completion = completion;
    }

    @Override
    protected Visitor nextVisitor(ElementPattern<? extends PsiElement> capture, VisitingContext context) {
        return new CompletionSchemaVisitor(completion, capture, context);
    }

    @Override
    protected void handleContainer(Container container, ElementPattern<? extends PsiElement> captureElement) {
        List<Choice> choices = new LinkedList<Choice>();

        for(Property property : container.getProperties()) {
            for (String name : property.nameExamples()) {
                choices.add(new Choice(name, propertyDescriptionProvider.getDescription(property)));
            }
        }

        completion.extend(
            CompletionType.BASIC,
            captureElement,
            new ChoiceCompletionProvider(choices, KeyInsertHandler.INSTANCE)
        );
    }

    @Override
    public void visitOneOf(OneOf oneOf) {
        for(Element element : oneOf.getElements()) {
            element.accept(this);
        }
    }

    @Override
    public void visitScalarChoicesValue(Scalar.Choices choices) {
        completion.extend(
            CompletionType.BASIC,
            YamlPatterns.scalarValue().withSuperParent(2, capture),
            ChoiceCompletionProvider.fromChoiceNames(choices.getChoices(), insertHandler)
        );
    }

    @Override
    public void visitScalarPropertiesFromPathValue(Scalar.PropertiesFromPath propertiesFromPath) {
        completion.extend(
            CompletionType.BASIC,
            context == VisitingContext.PROPERTY_VALUE ? YamlPatterns.scalarValue().withSuperParent(2, capture) : capture,
            new ChoicesFromPathCompletionProvider(propertiesFromPath.getPath(), propertiesFromPath.getPrefix(), insertHandler)
        );
    }

    @Override
    public void visitScalarConditionValue(Scalar.Condition condition) {
        completion.extend(
            CompletionType.BASIC,
            capture,
            new ConditionCompletionProvider(insertHandler)
        );
    }

    @Override
    public void visitScalarActionValue(Scalar.Action action) {
        completion.extend(
            CompletionType.BASIC,
            capture,
            new ActionCompletionProvider(insertHandler)
        );
    }

    @Override
    public void visitScalarDatagridValue(Scalar.Datagrid datagrid) {
        completion.extend(
            CompletionType.BASIC,
            YamlPatterns.scalarValue().withSuperParent(2, capture),
            new DatagridCompletionProvider()
        );
    }
}
