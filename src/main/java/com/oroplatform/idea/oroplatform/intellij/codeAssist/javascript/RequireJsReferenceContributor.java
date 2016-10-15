package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.StaticStringWrapperProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.WrappedFileReferenceProvider;
import com.oroplatform.idea.oroplatform.StringWrapper;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.fileInProjectWithPluginEnabled;

public class RequireJsReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        final PsiElementPattern.Capture<JSLiteralExpression> pattern =
            psiElement(JSLiteralExpression.class).inFile(fileInProjectWithPluginEnabled())
                .and(
                    psiElement().andOr(
                        psiElement()
                            .withSuperParent(2, functionCall("require")),
                        psiElement()
                            .withSuperParent(3, functionCall("define"))
                    )
                );

        registrar.registerReferenceProvider(pattern, new WrappedFileReferenceProvider(
            new StaticStringWrapperProvider(new StringWrapper("oroui/js/", ".js")),
            new UIBundleJsRootDirFinder())
        );
    }

    private PsiElementPattern.Capture<JSCallExpression> functionCall(String name) {
        return psiElement(JSCallExpression.class).withChild(psiElement().withText(name));
    }
}
