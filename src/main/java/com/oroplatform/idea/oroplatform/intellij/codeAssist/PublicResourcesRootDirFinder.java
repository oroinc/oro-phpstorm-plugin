package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class PublicResourcesRootDirFinder implements RootDirFinder {
    @Override
    @Nullable
    public VirtualFile getRootDir(PsiElement element) {
        final VirtualFile resourcesDir = getFirstAncestorByName(element.getContainingFile().getOriginalFile().getVirtualFile(), "Resources");

        if(resourcesDir == null) return null;

        return resourcesDir.findChild("public");
    }

    private VirtualFile getFirstAncestorByName(VirtualFile file, String name) {
        while(file != null && !file.getName().equals(name)) {
            file = file.getParent();
        }

        return file;
    }
}
