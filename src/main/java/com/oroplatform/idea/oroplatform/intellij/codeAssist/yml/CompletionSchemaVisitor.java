package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ChoiceCompletionProvider;
import com.oroplatform.idea.oroplatform.schema.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLCompoundValue;
import org.jetbrains.yaml.psi.YAMLHash;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLSequence;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;

class CompletionSchemaVisitor implements Visitor {
    private final CompletionContributor completion;
    private final ElementPattern<? extends PsiElement> capture;

    CompletionSchemaVisitor(CompletionContributor completion, ElementPattern<? extends PsiElement> capture) {
        this.completion = completion;
        this.capture = capture;
    }

    @Override
    public void visitArray(Array array) {
        array.getType().accept(new CompletionSchemaVisitor(
            completion,
            psiElement(YAMLSequence.class).withSuperParent(2, capture))
        );
    }

    @Override
    public void visitContainer(Container container) {
        List<String> properties = new LinkedList<String>();

        for(Property property : container.getProperties()) {
            properties.addAll(property.nameExamples());
        }

        ElementPattern<? extends PsiElement> newCapture = psiElement().withParent(capture);

        completion.extend(
            CompletionType.BASIC,
            psiElement().andOr(
                getKeyPattern(newCapture),
                getKeyPattern(psiElement(YAMLHash.class).withParent(psiElement(YAMLCompoundValue.class).withParent(newCapture)))
            ),
            new ChoiceCompletionProvider(properties)
        );

        for(final Property property : container.getProperties()) {
            PsiElementPattern.Capture<PsiElement> propertyCapture = psiElement().withName(string().with(new PatternCondition<String>(null) {
                @Override
                public boolean accepts(@NotNull String s, ProcessingContext context) {
                    return property.nameMatches(s);
                }
            }));
            ElementPattern<? extends PsiElement> captureForNextVisitor = psiElement().andOr(
                psiElement().and(newCapture).withFirstChild(propertyCapture),
                propertyCapture.withParent(newCapture),
                propertyCapture.withParent(psiElement(YAMLHash.class).withSuperParent(2, newCapture))
            );
            property.getValueElement().accept(new CompletionSchemaVisitor(completion, captureForNextVisitor));
        }
    }

    private PsiElementPattern.Capture<PsiElement> getKeyPattern(ElementPattern<? extends PsiElement> parent) {
        return psiElement().andOr(
            psiElement(YAMLTokenTypes.TEXT).withParent(parent),
            psiElement(YAMLTokenTypes.TEXT).withParent(parent).afterSiblingSkipping(psiElement().andNot(psiElement(YAMLTokenTypes.EOL)), psiElement(YAMLTokenTypes.EOL)),
            psiElement(YAMLTokenTypes.SCALAR_KEY).withParent(psiElement(YAMLKeyValue.class).withSuperParent(2, parent)),
            psiElement(YAMLTokenTypes.SCALAR_KEY).withParent(psiElement(YAMLKeyValue.class).withParent(parent).withParent(psiElement(YAMLHash.class)))
        );
    }

    @Override
    public void visitLiteral(Literal literal) {
        completion.extend(
            CompletionType.BASIC,
            psiElement(YAMLTokenTypes.TEXT).withParent(capture),
            getCompletionProviderFor(literal.getValue())
        );
    }

    //TODO: refactor when necessary
    private CompletionProvider<CompletionParameters> getCompletionProviderFor(Literal.Value literalValue) {
        if(literalValue instanceof Literal.Choices) {
            return new ChoiceCompletionProvider(((Literal.Choices) literalValue).getChoices());
        } else {
            return new ChoiceCompletionProvider(Collections.<String>emptyList());
        }
    }
}
