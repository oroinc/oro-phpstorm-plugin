package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.StringWrapper;

public class StaticStringWrapperProvider implements StringWrapperProvider {
    private final StringWrapper stringWrapper;

    public StaticStringWrapperProvider(StringWrapper stringWrapper) {
        this.stringWrapper = stringWrapper;
    }

    @Override
    public StringWrapper getStringWrapperFor(PsiElement requestElement, VirtualFile sourceDir) {
        return stringWrapper;
    }
}
