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

public class ServiceAliasReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String aliasTag;
    @NotNull
    private final String text;
    private final InsertHandler<LookupElement> insertHandler;
    private final ServicesIndex servicesIndex;

    public ServiceAliasReference(String aliasTag, PsiElement psiElement, @NotNull String text, InsertHandler<LookupElement> insertHandler) {
        super(psiElement);
        this.aliasTag = aliasTag;
        this.text = text.replace(PsiElements.IN_PROGRESS_VALUE, "").trim().replace("\\\\", "\\");;
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
        return servicesIndex.getServiceAliasesByTag(aliasTag).stream()
            .map(service -> LookupElementBuilder.create(service).withInsertHandler(insertHandler))
            .toArray();
    }
}
