package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ChoicesProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.schema.DefaultValueDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

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

        choicesProvider.getChoices(parameters.getPosition()).stream()
            .filter(choice -> !existingProperties.contains(choice.getName()))
            .map(choice -> {
                //it is parent in order to get element from higher level of abstraction - YAMLElement
                final PsiElement element = parameters.getPosition().getParent();
                final LookupElementBuilder lookupElement = LookupElementBuilder.create(element, choice.getName())
                        .withInsertHandler(insertHandler)
                        .withTypeText(choice.getDescription(), true)
                        .withStrikeoutness(choice.isDeprecated())
                        .withIcon(choice.getIcon());
                lookupElement.putUserData(DefaultValueDescriptor.KEY, choice.getDefaultValueDescriptor());
                return lookupElement;
            })
            .forEach(result::addElement);
    }

    @NotNull
    private Set<String> getExistingChoices(@NotNull CompletionParameters parameters) {
        return toStream(YamlPsiElements.getFirstMapping(parameters.getPosition(), /* set max depth in order to skip mapping if property is not followed by colon */ 3))
            .flatMap(mapping -> mapping.getKeyValues().stream())
            .map(YAMLKeyValue::getKeyText)
            .collect(Collectors.toSet());
    }

}
