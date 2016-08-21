package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

import java.util.List;

public interface CompletionProviders {
    CompletionProvider<CompletionParameters> action(InsertHandler<LookupElement> insertHandler);
    CompletionProvider<CompletionParameters> condition(InsertHandler<LookupElement> insertHandler);
    CompletionProvider<CompletionParameters> formType(InsertHandler<LookupElement> insertHandler);
    CompletionProvider<CompletionParameters> datagrid(InsertHandler<LookupElement> insertHandler);
    CompletionProvider<CompletionParameters> acl(InsertHandler<LookupElement> insertHandler);
    CompletionProvider<CompletionParameters> service(InsertHandler<LookupElement> insertHandler);
    CompletionProvider<CompletionParameters> massActionProvider(InsertHandler<LookupElement> insertHandler);
    CompletionProvider<CompletionParameters> propertiesFromPath(PropertyPath path, String prefix, InsertHandler<LookupElement> insertHandler);
    CompletionProvider<CompletionParameters> choices(List<String> choices, InsertHandler<LookupElement> insertHandler);
}
