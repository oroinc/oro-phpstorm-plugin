package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.*;
import com.oroplatform.idea.oroplatform.schema.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;

class FileReferenceVisitor extends YamlVisitor {
    private final PsiReferenceRegistrar registrar;

    FileReferenceVisitor(PsiReferenceRegistrar registrar, ElementPattern<? extends PsiElement> capture, VisitingContext context) {
        super(capture, context);
        this.registrar = registrar;
    }

    @Override
    protected Visitor nextVisitor(ElementPattern<? extends PsiElement> capture, VisitingContext context) {
        return new FileReferenceVisitor(registrar, capture, context);
    }

    @Override
    protected void handleContainer(Container container, ElementPattern<? extends PsiElement> capture) {
    }

    @Override
    public void visitOneOf(OneOf oneOf) {
        for(Element element : oneOf.getElements()) {
            element.accept(this);
        }
    }

    @Override
    public void visitScalarFileValue(Scalar.File file) {
        registrar.registerReferenceProvider(
            psiElement().andOr(
                capture,
                psiElement().withParent(capture)
            ),
            new FilePathReferenceProvider()
        );
    }

}
