package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceRegistrar;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceProviders;
import com.oroplatform.idea.oroplatform.schema.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;

class ReferenceVisitor extends YamlVisitor {

    private final PsiReferenceRegistrar registrar;
    private final ReferenceProviders referenceProviders;

    ReferenceVisitor(ReferenceProviders referenceProviders, PsiReferenceRegistrar registrar, ElementPattern<? extends PsiElement> capture, VisitingContext context) {
        super(capture, context);
        this.referenceProviders = referenceProviders;
        this.registrar = registrar;
    }

    @Override
    protected Visitor nextVisitor(ElementPattern<? extends PsiElement> capture, VisitingContext context) {
        return new ReferenceVisitor(referenceProviders, registrar, capture, context);
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
    public void visitScalar(Scalar scalar) {
        scalar.getProvider(referenceProviders, insertHandler).ifPresent(provider -> {
            registrar.registerReferenceProvider(
                context == VisitingContext.PROPERTY_VALUE ? YamlPatterns.scalarValue().andOr(capture, psiElement().withParent(capture)) : capture,
                provider
            );
        });
    }
}
