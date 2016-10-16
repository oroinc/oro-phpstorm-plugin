package com.oroplatform.idea.oroplatform.intellij.codeAssist.completionProvider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.indexes.AssetsFiltersIndex;
import org.jetbrains.annotations.NotNull;

public class AssetsFilterCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final InsertHandler<LookupElement> insertHandler;

    public AssetsFilterCompletionProvider(InsertHandler<LookupElement> insertHandler) {
        this.insertHandler = insertHandler;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        final AssetsFiltersIndex index = AssetsFiltersIndex.instance(parameters.getOriginalFile().getProject());

        for (String filter : index.getFilters()) {
            result.addElement(LookupElementBuilder.create(filter).withInsertHandler(insertHandler));
        }
    }
}
