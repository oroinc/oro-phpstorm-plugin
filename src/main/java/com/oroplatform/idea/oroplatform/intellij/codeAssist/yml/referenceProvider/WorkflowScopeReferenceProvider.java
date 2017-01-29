package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.WorkflowScopeReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLScalar;

public class WorkflowScopeReferenceProvider extends PsiReferenceProvider {
    private final InsertHandler<LookupElement> insertHandler;

    public WorkflowScopeReferenceProvider(InsertHandler<LookupElement> insertHandler) {
        this.insertHandler = insertHandler;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLScalar) {
            final YAMLScalar keyValue = (YAMLScalar) element;
            return new PsiReference[] {
                new WorkflowScopeReference(keyValue, keyValue.getTextValue(), insertHandler)
            };
        }
        return new PsiReference[0];
    }
}
