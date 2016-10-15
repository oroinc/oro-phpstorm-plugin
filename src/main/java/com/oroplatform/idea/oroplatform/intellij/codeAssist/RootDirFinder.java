package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface RootDirFinder {
    @Nullable
    VirtualFile getRootDir(PsiElement element);
}
