package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.StringWrapper;

public interface StringWrapperProvider {
    StringWrapper getStringWrapperFor(PsiElement requestElement, VirtualFile sourceDirectory);
}
