package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;

public class RequirejsReference extends PsiPolyVariantReferenceBase<PsiElement> {

    private final String text;

    public RequirejsReference(PsiElement element, String text) {
        super(element);
        this.text = text;
    }

    //TODO: use configuration from RequireJsComponent

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        //TODO: use Path.resolve implementation from requirejs plugin
        return new ResolveResult[0];
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        //TODO: use RequirejsProjectComponent.getCompletion from requirejs plugin
        return new Object[0];
    }
}
