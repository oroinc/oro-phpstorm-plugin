package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.StringWrapper;
import org.jetbrains.annotations.NotNull;

public interface StringWrapperProvider {
    StringWrapper getStringWrapperFor(@NotNull PsiElement requestElement, @NotNull VirtualFile sourceDirectory);
}
