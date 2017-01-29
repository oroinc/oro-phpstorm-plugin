package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import org.jetbrains.annotations.NotNull;

public class WorkflowScopeReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String text;
    private final InsertHandler<LookupElement> insertHandler;

    public WorkflowScopeReference(PsiElement element, String text, InsertHandler<LookupElement> insertHandler) {
        super(element);
        this.text = text;
        this.insertHandler = insertHandler;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        //TODO
        return new ResolveResult[0];
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Project project = myElement.getProject();
        return ServicesIndex.instance(project).findWorkflowScopes().stream()
            .map(scope -> LookupElementBuilder.create(scope.name).withInsertHandler(insertHandler))
            .toArray();
    }
}
