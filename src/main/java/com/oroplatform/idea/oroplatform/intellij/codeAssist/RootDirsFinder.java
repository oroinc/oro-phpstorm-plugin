package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.Collection;

public interface RootDirsFinder {
    Collection<VirtualFile> getRootDirs(PsiElement element);
}
