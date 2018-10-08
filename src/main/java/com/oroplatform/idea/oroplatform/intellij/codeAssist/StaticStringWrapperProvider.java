package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.StringWrapper;
import org.jetbrains.annotations.NotNull;

public class StaticStringWrapperProvider implements StringWrapperProvider {
    private final StringWrapper stringWrapper;

    public StaticStringWrapperProvider(StringWrapper stringWrapper) {
        this.stringWrapper = stringWrapper;
    }

    @Override
    public StringWrapper getStringWrapperFor(@NotNull PsiElement requestElement, @NotNull VirtualFile sourceDir) {
        return stringWrapper;
    }
}
