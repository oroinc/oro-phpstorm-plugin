package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLPsiElement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.*;

class ChoicesFromPathCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final PropertyPath path;
    private final String prefix;

    ChoicesFromPathCompletionProvider(PropertyPath path, String prefix) {
        this.path = path;
        this.prefix = prefix;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        final YAMLFile file = (YAMLFile) parameters.getPosition().getContainingFile();
        final Set<PsiElement> ancestors = getAncestors(parameters.getPosition());
        final Collection<String> properties = getPropertiesFrom(path, YamlPsiElements.getMappingsFrom(file), ancestors);

        for (String property : properties) {
            result.addElement(LookupElementBuilder.create(prefix+property));
        }
    }

    private Collection<String> getPropertiesFrom(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(path.getProperties().isEmpty()) {
            return getPropertiesFrom(elements);
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getPropertiesFrom(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
    }

    @NotNull
    private Collection<YAMLPsiElement> getElementsForProperty(PropertyPath.Property property, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        final Collection<YAMLPsiElement> newElements = new LinkedList<YAMLPsiElement>();

        if(property.isThis()) {
            for (YAMLKeyValue keyValue : getKeyValuesFrom(elements)) {
                if(keyValue.getValue() != null && ancestors.contains(keyValue.getValue())) {
                    newElements.add(keyValue.getValue());
                }
            }
        } else {
            for (YAMLMapping mapping : filterMappings(elements)) {
                final YAMLKeyValue keyValue = mapping.getKeyValueByKey(property.getName());
                if(keyValue != null && keyValue.getValue() != null) {
                    newElements.add(keyValue.getValue());
                }
            }
        }
        return newElements;
    }

    private Collection<String> getPropertiesFrom(Collection<? extends YAMLPsiElement> elements) {
        Collection<String> properties = new THashSet<String>();

        for (YAMLMapping mapping : filterMappings(elements)) {
            for (YAMLKeyValue keyValue : YamlPsiElements.getKeyValuesFrom(mapping)) {
                properties.add(keyValue.getKeyText());
            }
        }

        return properties;
    }
}
