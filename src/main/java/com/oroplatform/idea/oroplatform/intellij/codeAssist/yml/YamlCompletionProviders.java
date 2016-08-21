package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.completionProvider.*;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider.ChoiceCompletionProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider.ChoicesFromPathCompletionProvider;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

import java.util.List;

class YamlCompletionProviders implements CompletionProviders {
    @Override
    public CompletionProvider<CompletionParameters> action(InsertHandler<LookupElement> insertHandler) {
        return new ActionCompletionProvider(insertHandler);
    }

    @Override
    public CompletionProvider<CompletionParameters> condition(InsertHandler<LookupElement> insertHandler) {
        return new ConditionCompletionProvider(insertHandler);
    }

    @Override
    public CompletionProvider<CompletionParameters> formType(InsertHandler<LookupElement> insertHandler) {
        return new FormTypeCompletionProvider();
    }

    @Override
    public CompletionProvider<CompletionParameters> datagrid(InsertHandler<LookupElement> insertHandler) {
        return new DatagridCompletionProvider();
    }

    @Override
    public CompletionProvider<CompletionParameters> acl(InsertHandler<LookupElement> insertHandler) {
        return new AclCompletionProvider();
    }

    @Override
    public CompletionProvider<CompletionParameters> service(InsertHandler<LookupElement> insertHandler) {
        return new ServiceCompletionProvider(insertHandler);
    }

    @Override
    public CompletionProvider<CompletionParameters> massActionProvider(InsertHandler<LookupElement> insertHandler) {
        return new MassActionProviderCompletionProvider(insertHandler);
    }

    @Override
    public CompletionProvider<CompletionParameters> propertiesFromPath(PropertyPath path, String prefix, InsertHandler<LookupElement> insertHandler) {
        return new ChoicesFromPathCompletionProvider(path, prefix, insertHandler);
    }

    @Override
    public CompletionProvider<CompletionParameters> choices(List<String> choices, InsertHandler<LookupElement> insertHandler) {
        return ChoiceCompletionProvider.fromChoiceNames(choices, insertHandler);
    }
}
