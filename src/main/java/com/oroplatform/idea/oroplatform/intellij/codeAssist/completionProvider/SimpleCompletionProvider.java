package com.oroplatform.idea.oroplatform.intellij.codeAssist.completionProvider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

public class SimpleCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final InsertHandler<LookupElement> insertHandler;
    private final Function<Project, Collection<String>> getLookupStrings;

    public SimpleCompletionProvider(InsertHandler<LookupElement> insertHandler, Function<Project, Collection<String>> getLookupStrings) {
        this.insertHandler = insertHandler;
        this.getLookupStrings = getLookupStrings;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        final Project project = parameters.getOriginalFile().getProject();

        final Collection<String> elements = getLookupStrings.apply(project);

        for (String element : elements) {
            result.addElement(LookupElementBuilder.create(element).withInsertHandler(insertHandler));
        }
    }
}
