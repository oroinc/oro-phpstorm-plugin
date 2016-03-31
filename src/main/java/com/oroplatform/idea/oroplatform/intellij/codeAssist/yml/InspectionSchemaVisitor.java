package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.OroPlatformBundle;
import com.oroplatform.idea.oroplatform.schema.*;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.*;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.PsiElements.*;

class InspectionSchemaVisitor extends VisitorAdapter {
    private final ProblemsHolder problems;
    private final List<PsiElement> elements = new LinkedList<PsiElement>();

    InspectionSchemaVisitor(ProblemsHolder problems, List<PsiElement> elements) {
        this.problems = problems;
        this.elements.addAll(elements);
    }

    @Override
    public void visitSequence(Sequence sequence) {

    }

    @Override
    public void visitContainer(Container container) {
        for(YAMLMapping element : getMappings(elements)) {
            for(Property property : container.getProperties()) {
                for(YAMLKeyValue keyValue : getKeyValuesFrom(element)) {
                    if(property.nameMatches(keyValue.getName())) {
                        Visitor visitor = new InspectionSchemaVisitor(problems, Collections.<PsiElement>singletonList(keyValue.getValue()));
                        property.getValueElement().accept(visitor);
                    }
                }
            }
        }
    }

    @Override
    public void visitLiteralChoicesValue(Scalar.Choices choices) {
        for(YAMLScalar element : getScalars(elements)) {
            if(!choices.getChoices().contains(element.getTextValue())) {
                problems.registerProblem(element, OroPlatformBundle.message("inspection.schema.notAllowedPropertyValue", element.getTextValue(), StringUtil.join(choices.getChoices(), ", ")));
            }
        }
    }
}
