package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.lang.javascript.JSElementType;
import com.intellij.lang.javascript.navigation.JSGotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

public class RequireJsGoToDeclarationHandler extends JSGotoDeclarationHandler {
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

        PsiElement[] standardElements = super.getGotoDeclarationTargets(sourceElement, offset, editor);

        return Stream.concat(Arrays.stream(((PsiMultiReference) multiReference).multiResolve(true))
                        .filter(ResolveResult::isValidResult)
                        .map(ResolveResult::getElement),
                Arrays.stream(standardElements != null ? standardElements : new PsiElement[0])
        ).toArray(PsiElement[]::new);

    }
}
