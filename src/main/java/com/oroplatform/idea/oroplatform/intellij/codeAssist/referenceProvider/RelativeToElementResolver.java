package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

public class RelativeToElementResolver implements RelativeDirectoryResolver {

    private final String relativePath;

    public RelativeToElementResolver(String relativePath) {
        this.relativePath = relativePath;
    }

    public RelativeToElementResolver() {
        this(null);
    }

    @Override
    public PsiDirectory resolve(PsiElement element) {
        final PsiFile file = element.getContainingFile();

        return relativePath == null ? file.getParent() : getRelative(file.getOriginalFile(), relativePath);
    }

    private static PsiDirectory getRelative(PsiFile file, String relativePath) {
        final VirtualFile relativeFile = VfsUtil.findRelativeFile(relativePath, file.getVirtualFile());
        if(relativeFile == null) return null;

        return PsiManager.getInstance(file.getProject()).findDirectory(relativeFile);
    }

}
