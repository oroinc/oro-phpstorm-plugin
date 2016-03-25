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
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLCompoundValue;
import org.jetbrains.yaml.psi.YAMLHash;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLSequence;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;

abstract class YmlVisitor implements Visitor {
    final ElementPattern<? extends PsiElement> capture;

    YmlVisitor(ElementPattern<? extends PsiElement> capture) {
        this.capture = capture;
    }

    @Override
    public void visitArray(Array array) {
        array.getType().accept(nextVisitor(
            psiElement(YAMLSequence.class).withSuperParent(2, capture))
        );
    }

    protected abstract Visitor nextVisitor(ElementPattern<? extends PsiElement> capture);

    @Override
    public void visitContainer(Container container) {
        ElementPattern<? extends PsiElement> newCapture = psiElement().withParent(capture);

        handleContainer(
            container,
            psiElement().andOr(
                getKeyPattern(newCapture),
                getKeyPattern(psiElement(YAMLHash.class).withParent(psiElement(YAMLCompoundValue.class).withParent(newCapture)))
            )
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
            property.getValueElement().accept(nextVisitor(captureForNextVisitor));
        }
    }

    protected abstract void handleContainer(Container container, ElementPattern<? extends PsiElement> capture);

    private PsiElementPattern.Capture<PsiElement> getKeyPattern(ElementPattern<? extends PsiElement> parent) {
        return psiElement().andOr(
            psiElement(YAMLTokenTypes.TEXT).withParent(parent),
            psiElement(YAMLTokenTypes.TEXT).withParent(parent).afterSiblingSkipping(psiElement().andNot(psiElement(YAMLTokenTypes.EOL)), psiElement(YAMLTokenTypes.EOL)),
            psiElement(YAMLTokenTypes.SCALAR_KEY).withParent(psiElement(YAMLKeyValue.class).withSuperParent(2, parent)),
            psiElement(YAMLTokenTypes.SCALAR_KEY).withParent(psiElement(YAMLKeyValue.class).withParent(parent).withParent(psiElement(YAMLHash.class)))
        );
    }
}
