package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class LeafPsiElementManipulator extends AbstractElementManipulator<LeafPsiElement> {
    @Override
    public LeafPsiElement handleContentChange(@NotNull LeafPsiElement element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return element;
    }
}
