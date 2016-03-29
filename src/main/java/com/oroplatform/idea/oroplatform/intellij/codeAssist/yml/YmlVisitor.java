package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.schema.Array;
import com.oroplatform.idea.oroplatform.schema.Container;
import com.oroplatform.idea.oroplatform.schema.Property;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;

abstract class YmlVisitor implements Visitor {
    final ElementPattern<? extends PsiElement> capture;

    YmlVisitor(ElementPattern<? extends PsiElement> capture) {
        this.capture = capture;
    }

    @Override
    public void visitArray(Array array) {
        array.getType().accept(nextVisitor(YmlPatterns.sequence(capture)));
    }

    protected abstract Visitor nextVisitor(ElementPattern<? extends PsiElement> capture);

    @Override
    public void visitContainer(Container container) {
        ElementPattern<? extends PsiElement> newCapture = YmlPatterns.mapping(capture);

        handleContainer(container, YmlPatterns.keyInProgress(capture, newCapture));

        for(final Property property : container.getProperties()) {
            PsiElementPattern.Capture<PsiElement> propertyCapture = psiElement().withName(string().with(new PatternCondition<String>(null) {
                @Override
                public boolean accepts(@NotNull String s, ProcessingContext context) {
                    return property.nameMatches(s);
                }
            }));
            ElementPattern<? extends PsiElement> captureForNextVisitor = propertyCapture.withParent(newCapture);
            property.getValueElement().accept(nextVisitor(captureForNextVisitor));
        }
    }

    protected abstract void handleContainer(Container container, ElementPattern<? extends PsiElement> capture);
}
