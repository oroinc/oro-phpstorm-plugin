package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import org.jetbrains.annotations.NotNull;

public class WorkflowScopeReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String text;
    private final InsertHandler<LookupElement> insertHandler;
    private final Project project;

    public WorkflowScopeReference(PsiElement element, String text, InsertHandler<LookupElement> insertHandler) {
        super(element);
        this.text = text.replace(PsiElements.IN_PROGRESS_VALUE, "").trim();
        this.insertHandler = insertHandler;
        this.project = element.getProject();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return ServicesIndex.instance(project).findWorkflowScopes().stream()
            .filter(scope -> text.equals(scope.name))
            .map(scope -> new PsiElementResolveResult(scope.phpClass))
            .toArray(ResolveResult[]::new);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return ServicesIndex.instance(project).findWorkflowScopes().stream()
            .map(scope -> LookupElementBuilder.create(scope.name).withInsertHandler(insertHandler))
            .toArray();
    }
}
