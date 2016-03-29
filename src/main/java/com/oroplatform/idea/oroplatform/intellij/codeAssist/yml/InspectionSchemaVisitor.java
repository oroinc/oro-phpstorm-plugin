package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.schema.Array;
import com.oroplatform.idea.oroplatform.schema.Container;
import com.oroplatform.idea.oroplatform.schema.Literal;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import org.jetbrains.yaml.psi.YAMLPsiElement;

import java.util.LinkedList;
import java.util.List;

//TODO: Implement schema inspection
class InspectionSchemaVisitor implements Visitor {
    private final List<ProblemDescriptor> collectedProblems;
    private final ElementPattern<? extends PsiElement> capture;
    private final List<YAMLPsiElement> elements = new LinkedList<YAMLPsiElement>();

    InspectionSchemaVisitor(List<ProblemDescriptor> collectedProblems, List<YAMLPsiElement> elements, ElementPattern<? extends PsiElement> parentPattern) {
        this.collectedProblems = collectedProblems;
        this.capture = parentPattern;
        this.elements.addAll(elements);
    }

    @Override
    public void visitArray(Array array) {

    }

    @Override
    public void visitContainer(Container container) {

    }

    @Override
    public void visitLiteral(Literal literal) {

    }
}
