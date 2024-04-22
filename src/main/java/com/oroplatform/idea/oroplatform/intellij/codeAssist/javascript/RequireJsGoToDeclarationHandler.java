package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.lang.javascript.JSElementType;
import com.intellij.lang.javascript.frameworks.modules.JSFileModuleReference;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.tree.IElementType;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.WrappedFileReferenceProvider;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class RequireJsGoToDeclarationHandler implements GotoDeclarationHandler {
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        IElementType elementType = sourceElement.getNode().getElementType();

        if (!(elementType instanceof JSElementType)) {
            return new PsiElement[0];
        }

        PsiReference multiReference = sourceElement.findReferenceAt(sourceElement.getText().length() - 1);

        if (!(multiReference instanceof PsiMultiReference)) {
            return new PsiElement[0];
        }

        return Arrays.stream(((PsiMultiReference) multiReference).getReferences())
                .filter(psiReference -> psiReference instanceof FileReference && !(psiReference instanceof JSFileModuleReference))
                .map(psiReference -> psiReference.resolve())
                .toArray(PsiElement[]::new);
    }

    @Override
    public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getActionText(@NotNull DataContext context) {
        return GotoDeclarationHandler.super.getActionText(context);
    }
}
