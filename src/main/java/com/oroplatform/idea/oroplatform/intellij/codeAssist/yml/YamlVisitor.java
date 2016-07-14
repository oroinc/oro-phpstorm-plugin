package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.schema.*;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;

abstract class YamlVisitor implements Visitor {
    final ElementPattern<? extends PsiElement> capture;
    final VisitingContext context;
    final InsertHandler<LookupElement> insertHandler;

    YamlVisitor(ElementPattern<? extends PsiElement> capture, VisitingContext context) {
        this.capture = capture;
        this.context = context;
        this.insertHandler = context == VisitingContext.PROPERTY_KEY ? KeyInsertHandler.INSTANCE : null;
    }

    @Override
    public void visitSequence(Sequence sequence) {
        sequence.getType().accept(nextVisitor(YamlPatterns.sequence(capture)));
    }

    protected abstract Visitor nextVisitor(ElementPattern<? extends PsiElement> capture, VisitingContext context);

    private Visitor nextVisitor(ElementPattern<? extends PsiElement> capture) {
        return nextVisitor(capture, context);
    }

    @Override
    public void visitContainer(Container container) {
        final ElementPattern<? extends PsiElement> newCapture = YamlPatterns.mapping(capture);

        handleContainer(container, YamlPatterns.keyInProgress(capture, newCapture));

        for(final Property property : container.getProperties()) {
            final PsiElementPattern.Capture<PsiElement> propertyCapture = psiElement().withName(string().with(new PatternCondition<String>(null) {
                @Override
                public boolean accepts(@NotNull String s, ProcessingContext context) {
                    return property.nameMatches(s);
                }
            }));
            final ElementPattern<? extends PsiElement> captureForNextVisitor = propertyCapture.withParent(newCapture);
            property.getValueElement().accept(nextVisitor(captureForNextVisitor));
            property.getKeyElement().accept(nextVisitor(YamlPatterns.keyInProgress(capture, newCapture), VisitingContext.PROPERTY_KEY));
        }
    }

    protected abstract void handleContainer(Container container, ElementPattern<? extends PsiElement> capture);

    @Override
    public void visitRepeatAtAnyLevel(Repeated repeated) {
        repeated.getElement().accept(nextVisitor(psiElement().withAncestor(10, capture)));
    }

    enum VisitingContext {
        PROPERTY_KEY, PROPERTY_VALUE
    }
}
