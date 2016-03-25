package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.schema.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLArray;
import org.jetbrains.yaml.psi.YAMLCompoundValue;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static com.intellij.patterns.PlatformPatterns.*;

public class AclCompletion extends CompletionContributor {

    private final static Element SCHEMA = new Container(Arrays.asList(
            new Property(Pattern.compile(".+"),
                    new Container(Arrays.asList(
                            new Property("type", new Literal()),
                            new Property("class", new Literal()),
                            new Property("permission", new Literal()),
                            new Property("label", new Literal()),
                            new Property("group_name", new Literal()),
                            new Property("bindings", new Literal())
                    ))
            )
    ));

    public AclCompletion() {
        Visitor visitor = new CompletionSchemaVisitor(psiFile().withName("acl.yml"));
        SCHEMA.accept(visitor);
    }

    private class CompletionSchemaVisitor implements Visitor {
        private final ElementPattern<? extends PsiElement> capture;

        private CompletionSchemaVisitor(ElementPattern<? extends PsiElement> capture) {
            this.capture = capture;
        }

        @Override
        public void visitArray(Array array) {
            array.getType().accept(new CompletionSchemaVisitor(psiElement(YAMLArray.class).withParent(capture)));
        }

        @Override
        public void visitContainer(Container container) {
            List<String> properties = new LinkedList<String>();

            for(Property property : container.getProperties()) {
                properties.addAll(property.nameExamples());
            }

            ElementPattern<? extends PsiElement> newCapture = psiElement().withParent(capture);

            extend(
                CompletionType.BASIC,
                psiElement().andOr(
                    psiElement().withParent(newCapture),
                    psiElement().withParent(psiElement(YAMLCompoundValue.class).withParent(newCapture)),
                    psiElement(YAMLTokenTypes.SCALAR_KEY).withParent(psiElement(YAMLKeyValue.class).withSuperParent(2, newCapture))
                ),
                new ChoiceCompletionProvider(properties)
            );

            for(final Property property : container.getProperties()) {
                ElementPattern<? extends PsiElement> sss = psiElement().and(newCapture).withFirstChild(psiElement().withName(string().with(new PatternCondition<String>(null) {
                    @Override
                    public boolean accepts(@NotNull String s, ProcessingContext context) {
                        return property.nameMatches(s);
                    }
                })));
                property.getValueElement().accept(new CompletionSchemaVisitor(sss));
            }
        }

        @Override
        public void visitLiteral(Literal literal) {

        }
    }

    private class ChoiceCompletionProvider extends CompletionProvider<CompletionParameters> {

        private final List<String> choices = new LinkedList<String>();

        protected ChoiceCompletionProvider(List<String> choices) {
            this.choices.addAll(choices);
        }

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
            for(String choice : choices) {
                result.addElement(new BasicLookupElement(choice));
            }
        }
    }

    private static class BasicLookupElement extends LookupElement {

        private final String value;

        private BasicLookupElement(String value) {
            this.value = value;
        }

        @NotNull
        @Override
        public String getLookupString() {
            return value;
        }
    }
}
