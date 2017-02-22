package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ServiceAliasReference;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class ServiceAliasReferenceProvider extends PsiReferenceProvider {

    private final String aliasTag;
    private final InsertHandler<LookupElement> insertHandler;
    private final Function<ServicesIndex, Optional<Collection<String>>> getAllowedValues;
    private final String prefix;

    public ServiceAliasReferenceProvider(String aliasTag, InsertHandler<LookupElement> insertHandler, Function<ServicesIndex, Optional<Collection<String>>> getAllowedValues, String prefix) {
        this.aliasTag = aliasTag;
        this.insertHandler = insertHandler;
        this.getAllowedValues = getAllowedValues;
        this.prefix = prefix;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLKeyValue) {
            final YAMLKeyValue keyValue = (YAMLKeyValue) element;
            return new PsiReference[] { new ServiceAliasReference(aliasTag, keyValue.getKey(), keyValue.getKeyText(), prefix, getAllowedValues, insertHandler) };
        } else if(element instanceof YAMLScalar) {
            final YAMLScalar scalar = (YAMLScalar) element;
            return new PsiReference[] { new ServiceAliasReference(aliasTag, scalar, scalar.getTextValue(), prefix, getAllowedValues, insertHandler) };
        }

        return new PsiReference[0];
    }
}
