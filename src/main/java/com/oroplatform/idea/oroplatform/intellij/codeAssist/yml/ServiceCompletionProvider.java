package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import com.oroplatform.idea.oroplatform.schema.Scalar;
import com.oroplatform.idea.oroplatform.symfony.Service;
import com.oroplatform.idea.oroplatform.symfony.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

class ServiceCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final Scalar.Service serviceDefinition;

    ServiceCompletionProvider(Scalar.Service serviceDefinition) {
        this.serviceDefinition = serviceDefinition;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        final Project project = parameters.getOriginalFile().getProject();

        final Collection<Service> services = ServicesIndex.instance(project).findByTag(serviceDefinition.getTag());

        for (Service service : services) {
            for (Tag tag : service.getTags()) {
                if(tag.getAlias() != null) {
                    for (String alias : tag.getAlias().split("\\|")) {
                        result.addElement(LookupElementBuilder.create(serviceDefinition.getPrefix() + alias));
                    }
                }
            }
        }
    }
}
