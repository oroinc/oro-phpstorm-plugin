package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import com.intellij.patterns.PsiElementPattern;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class RequireJsReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        final PsiElementPattern.Capture<JSLiteralExpression> pattern =
            psiElement(JSLiteralExpression.class).withSuperParent(2, psiElement(JSCallExpression.class).withChild(psiElement().withText("require")));

        registrar.registerReferenceProvider(pattern, new RequireJsModuleReferenceProvider());
    }
}
