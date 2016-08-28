package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.intellij.indexes.RouteIndex;
import com.oroplatform.idea.oroplatform.symfony.Route;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

public class RouteReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String name;
    private final RouteIndex routeIndex;
    private final PhpIndex phpIndex;

    public RouteReference(PsiElement psiElement, String name) {
        super(psiElement);
        this.name = name;
        this.routeIndex = RouteIndex.instance(getElement().getProject());
        this.phpIndex = PhpIndex.getInstance(getElement().getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final Route route = routeIndex.findRoute(name);
        final Collection<ResolveResult> results = new LinkedList<ResolveResult>();

        if(route != null) {
            for (PhpClass phpClass : phpIndex.getClassesByFQN(route.getControllerName())) {
                for (Method method : phpClass.getMethods()) {
                    if(method.getName().equals(route.getAction())) {
                        results.add(new PsiElementResolveResult(method));
                    }
                }
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
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
