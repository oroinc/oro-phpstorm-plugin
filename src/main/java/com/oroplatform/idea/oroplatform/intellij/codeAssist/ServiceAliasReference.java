package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class ServiceAliasReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String aliasTag;
    @NotNull
    private final String text;
    private final Function<ServicesIndex, Optional<Collection<String>>> getAllowedValues;
    private final InsertHandler<LookupElement> insertHandler;
    private final ServicesIndex servicesIndex;

    public ServiceAliasReference(String aliasTag, PsiElement psiElement, @NotNull String text, Function<ServicesIndex, Optional<Collection<String>>> getAllowedValues, InsertHandler<LookupElement> insertHandler) {
        super(psiElement);
        this.aliasTag = aliasTag;
        this.text = text.replace(PsiElements.IN_PROGRESS_VALUE, "").trim().replace("\\\\", "\\");
        this.getAllowedValues = getAllowedValues;
        this.insertHandler = insertHandler;
        this.servicesIndex = ServicesIndex.instance(myElement.getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return servicesIndex.getServiceAliasClasses(aliasTag, text).stream()
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Optional<Collection<String>> allowedValues = getAllowedValues.apply(servicesIndex);
        return servicesIndex.getServiceAliasesByTag(aliasTag).stream()
            .filter(alias -> !allowedValues.isPresent() || allowedValues.filter(values -> values.contains(alias)).isPresent())
            .map(service -> LookupElementBuilder.create(service).withInsertHandler(insertHandler))
            .toArray();
    }
}
