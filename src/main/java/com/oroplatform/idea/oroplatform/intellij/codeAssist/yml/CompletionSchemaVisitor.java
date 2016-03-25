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
