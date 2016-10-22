package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.intellij.indexes.RouteIndex;
import org.jetbrains.annotations.NotNull;

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
        return routeIndex.findRoute(name).map(route ->
            phpIndex.getClassesByFQN(route.getControllerName()).stream()
                .flatMap(phpClass -> phpClass.getMethods().stream())
                .filter(method -> method.getName().equals(route.getAction()))
                .map(PsiElementResolveResult::new)
                .toArray(ResolveResult[]::new)
        ).orElseGet(() -> new ResolveResult[0]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return routeIndex.findRouteNames().stream()
            .map(name -> LookupElementBuilder.create(name).withIcon(Icons.ROUTE))
            .toArray();
    }
}
