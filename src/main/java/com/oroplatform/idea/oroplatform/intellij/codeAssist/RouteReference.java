package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.intellij.indexes.RouteIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

public class RouteReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String name;
    private final RouteIndex routeIndex;

    public RouteReference(PsiElement psiElement, String name) {
        super(psiElement);
        this.name = name;
        this.routeIndex = RouteIndex.instance(getElement().getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        //TODO: implementation
        return new ResolveResult[0];
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<LookupElement> results = new LinkedList<LookupElement>();

        for (String name : routeIndex.findRouteNames()) {
            results.add(LookupElementBuilder.create(name).withIcon(Icons.ROUTE));
        }

        return results.toArray();
    }
}
