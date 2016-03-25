package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ChoiceCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final List<String> choices = new LinkedList<String>();

    public ChoiceCompletionProvider(List<String> choices) {
        this.choices.addAll(choices);
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        for(String choice : choices) {
            result.addElement(new BasicLookupElement(choice));
        }
    }
}
