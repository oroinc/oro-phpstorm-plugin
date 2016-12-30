package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getPropertyFrom;

public class ChoicesFromPathCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final PropertyPath path;
    private final String prefix;
    private final InsertHandler<LookupElement> insertHandler;

    public ChoicesFromPathCompletionProvider(PropertyPath path, String prefix, InsertHandler<LookupElement> insertHandler) {
        this.path = path;
        this.prefix = prefix;
        this.insertHandler = insertHandler;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        final Collection<String> properties = getPropertyFrom(path, parameters.getPosition());

        for (String property : properties) {
            result.addElement(LookupElementBuilder.create(prefix+property).withInsertHandler(insertHandler));
        }
    }
}
