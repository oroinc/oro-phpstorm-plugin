package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Collection;
import java.util.Set;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getAncestors;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getPropertiesFrom;

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
        final YAMLFile file = (YAMLFile) parameters.getPosition().getContainingFile();
        final Set<PsiElement> ancestors = getAncestors(parameters.getPosition());
        final Collection<String> properties = getPropertiesFrom(path, YamlPsiElements.getMappingsFrom(file), ancestors);

        for (String property : properties) {
            result.addElement(LookupElementBuilder.create(prefix+property).withInsertHandler(insertHandler));
        }
    }
}
