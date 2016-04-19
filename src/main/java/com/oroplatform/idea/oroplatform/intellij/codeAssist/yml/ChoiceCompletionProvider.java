package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
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

    private final List<String> choices = new LinkedList<String>();
    private final InsertHandler<LookupElement> insertHandler;

    ChoiceCompletionProvider(List<String> choices, InsertHandler<LookupElement> insertHandler) {
        this.insertHandler = insertHandler;
        this.choices.addAll(choices);
    }

    ChoiceCompletionProvider(List<String> choices) {
        this(choices, null);
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        final YAMLMapping mapping = getFirstMapping(parameters.getPosition());
        final Set<String> existingProperties = new HashSet<String>();

        if (mapping != null) {
            for (YAMLKeyValue keyValue : mapping.getKeyValues()) {
                existingProperties.add(keyValue.getKeyText());
            }
        }

        for(String choice : choices) {
            if(!existingProperties.contains(choice)) {
                result.addElement(LookupElementBuilder.create(choice).withInsertHandler(insertHandler));
            }
        }
    }
}
