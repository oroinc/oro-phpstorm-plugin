package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.OroPlatformBundle;
import com.oroplatform.idea.oroplatform.schema.*;
import org.jetbrains.annotations.NotNull;
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
                boolean found = false;
                for(YAMLKeyValue keyValue : getKeyValuesFrom(element)) {
                    if(property.nameMatches(keyValue.getName())) {
                        found = true;
                        Visitor visitor = new InspectionSchemaVisitor(problems, Collections.<PsiElement>singletonList(keyValue.getValue()));
                        property.getValueElement().accept(visitor);
                    }
                }

                if(!found && property.isRequired()) {
                    for(YAMLKeyValue aaa : getKeyValues(Collections.singletonList(element.getParent()))) {
                        problems.registerProblem(aaa.getKey() == null ? aaa : aaa.getKey(), OroPlatformBundle.message("inspection.schema.required", property.getName()));
                    }
                }
            }
        }
    }

    @Override
    public void visitOneOf(OneOf oneOf) {
        final List<ProblemsHolder> problemsHolders = new LinkedList<ProblemsHolder>();
        for(Element element : oneOf.getElements()) {
            ProblemsHolder newProblems = new ProblemsHolder(problems.getManager(), problems.getFile(), problems.isOnTheFly());
            Visitor visitor = new InspectionSchemaVisitor(newProblems, elements);
            element.accept(visitor);
            problemsHolders.add(newProblems);
        }

        final NavigableSet<ProblemsHolder> sortedProblemsHolders = getSortedProblemHolders(problemsHolders);

        if(sortedProblemsHolders.size() > 0) {
            ProblemsHolder theShortestProblemsHolder = sortedProblemsHolders.first();
            for(ProblemDescriptor problemDescriptor : theShortestProblemsHolder.getResults()) {
                problems.registerProblem(problemDescriptor);
            }
        }
    }

    @NotNull
    private NavigableSet<ProblemsHolder> getSortedProblemHolders(List<ProblemsHolder> problemsHolders) {
        NavigableSet<ProblemsHolder> sortedProblemsHolders = new TreeSet<ProblemsHolder>(new Comparator<ProblemsHolder>() {
            @Override
            public int compare(ProblemsHolder o1, ProblemsHolder o2) {
                return o1.getResultCount() - o2.getResultCount();
            }
        });

        sortedProblemsHolders.addAll(problemsHolders);
        return sortedProblemsHolders;
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
