package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.PsiElements.getFirstMapping;

class ChoiceCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final List<Choice> choices = new LinkedList<Choice>();
    private final InsertHandler<LookupElement> insertHandler;

    static ChoiceCompletionProvider fromChoiceNames(List<String> choices, InsertHandler<LookupElement> insertHandler) {
        return new ChoiceCompletionProvider(toChoices(choices), insertHandler);
    }

    private static List<Choice> toChoices(List<String> names) {
        List<Choice> choices = new LinkedList<Choice>();
        for (String name : names) {
            choices.add(new Choice(name, null));
        }

        return choices;
    }

    ChoiceCompletionProvider(List<Choice> choices, InsertHandler<LookupElement> insertHandler) {
        this.insertHandler = insertHandler;
        this.choices.addAll(choices);
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        final YAMLMapping mapping = getFirstMapping(parameters.getPosition());
        final Set<String> existingProperties = new HashSet<String>();

        result = result.withPrefixMatcher(new PlainPrefixMatcher(result.getPrefixMatcher().getPrefix()));

        if (mapping != null) {
            for (YAMLKeyValue keyValue : mapping.getKeyValues()) {
                existingProperties.add(keyValue.getKeyText());
            }
        }

        for(Choice choice : choices) {
            if(!existingProperties.contains(choice.getName())) {
                result.addElement(LookupElementBuilder.create(choice.getName()).withInsertHandler(insertHandler).withTypeText(choice.getDescription(), true));
            }
        }
    }

    static class Choice {
        private final String name;
        private final String description;

        Choice(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        String getDescription() {
            return description;
        }
    }
}
