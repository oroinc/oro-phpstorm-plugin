package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.css.CssFileType;

import java.util.Collection;
import java.util.Collections;

public class LayoutAssetsCssOutputChoicesProvider implements ChoicesProvider {
    @Override
    public Collection<Choice> getChoices(PsiElement element) {
        final PsiDirectory layoutDir = element.getContainingFile().getOriginalFile().getContainingDirectory().getParent();

        if(layoutDir == null) return Collections.emptyList();

        final String name = layoutDir.getName();
        return Collections.singletonList(new Choice("css/layout/" + name + "/styles.css", null, CssFileType.INSTANCE.getIcon()));
    }
}
