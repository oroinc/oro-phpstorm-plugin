package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.Collection;
import java.util.Collections;

public class ElementRootDirsFinder implements RootDirsFinder {
    @Override
    public Collection<VirtualFile> getRootDirs(PsiElement element) {
        return Collections.singletonList(element.getOriginalElement().getContainingFile().getOriginalFile().getVirtualFile().getParent());
    }
}
