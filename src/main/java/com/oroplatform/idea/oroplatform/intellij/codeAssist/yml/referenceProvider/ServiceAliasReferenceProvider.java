package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ServiceAliasReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

public class ServiceAliasReferenceProvider extends PsiReferenceProvider {

    private final String aliasTag;
    private final InsertHandler<LookupElement> insertHandler;

    public ServiceAliasReferenceProvider(String aliasTag, InsertHandler<LookupElement> insertHandler) {
        this.aliasTag = aliasTag;
        this.insertHandler = insertHandler;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLScalar) {
            return new PsiReference[] { new ServiceAliasReference(aliasTag, element, ((YAMLScalar) element).getTextValue(), insertHandler) };
        } else if(element instanceof YAMLKeyValue && context.get("key") != null) {
            final YAMLKeyValue keyValue = (YAMLKeyValue) element;
            return new PsiReference[] { new ServiceAliasReference(aliasTag, keyValue.getKey(), keyValue.getKeyText(), insertHandler) };
        }

        return new PsiReference[0];
    }
}
