package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceRegistrar;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceProvider;
import com.oroplatform.idea.oroplatform.schema.Container;
import com.oroplatform.idea.oroplatform.schema.Literal;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import org.jetbrains.yaml.YAMLTokenTypes;

import static com.intellij.patterns.PlatformPatterns.psiElement;

class PhpReferenceVisitor extends YmlVisitor {

    private final PsiReferenceRegistrar registrar;

    PhpReferenceVisitor(PsiReferenceRegistrar registrar, ElementPattern<? extends PsiElement> capture) {
        super(capture);
        this.registrar = registrar;
    }

    @Override
    protected Visitor nextVisitor(ElementPattern<? extends PsiElement> capture) {
        return new PhpReferenceVisitor(registrar, capture);
    }

    @Override
    protected void handleContainer(Container container, ElementPattern<? extends PsiElement> capture) {
    }

    @Override
    public void visitLiteral(Literal literal) {
        //TODO: refactor
        if(literal.getValue() instanceof Literal.PhpClass) {
            registrar.registerReferenceProvider(capture, new PhpReferenceProvider());
        }

    }
}
