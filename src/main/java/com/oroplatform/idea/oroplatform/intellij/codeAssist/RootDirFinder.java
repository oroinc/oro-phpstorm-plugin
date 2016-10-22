package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.Optional;

public interface RootDirFinder {
    Optional<VirtualFile> getRootDir(PsiElement element);
}
