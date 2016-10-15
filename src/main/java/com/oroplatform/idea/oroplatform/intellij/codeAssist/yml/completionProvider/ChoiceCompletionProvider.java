package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ChoicesProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.HashSet;
import java.util.Set;

public class ChoiceCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final ChoicesProvider choicesProvider;
    private final InsertHandler<LookupElement> insertHandler;

    public ChoiceCompletionProvider(ChoicesProvider choicesProvider, InsertHandler<LookupElement> insertHandler) {
        this.choicesProvider = choicesProvider;
        this.insertHandler = insertHandler;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        final Set<String> existingProperties = getExistingChoices(parameters);
        result = result.withPrefixMatcher(new PlainPrefixMatcher(result.getPrefixMatcher().getPrefix()));

        for(ChoicesProvider.Choice choice : choicesProvider.getChoices(parameters.getOriginalPosition())) {
            if(!existingProperties.contains(choice.getName())) {
                result.addElement(
                    LookupElementBuilder.create(choice.getName())
                        .withInsertHandler(insertHandler)
                        .withTypeText(choice.getDescription(), true)
                        .withIcon(choice.getIcon())
                );
            }
        }
    }

    @NotNull
    private Set<String> getExistingChoices(@NotNull CompletionParameters parameters) {
        final YAMLMapping mapping = YamlPsiElements.getFirstMapping(parameters.getPosition(), /* set max depth in order to skip mapping if property is not followed by colon */ 3);
        final Set<String> existingProperties = new HashSet<String>();

        if (mapping != null) {
            for (YAMLKeyValue keyValue : mapping.getKeyValues()) {
                existingProperties.add(keyValue.getKeyText());
            }
        }
        return existingProperties;
    }

}
