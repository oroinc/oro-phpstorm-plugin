package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.Optional;

public class PublicResourcesRootDirFinder implements RootDirFinder {
    @Override
    public Optional<VirtualFile> getRootDir(PsiElement element) {
        return getFirstAncestorByName(element.getContainingFile().getOriginalFile().getVirtualFile(), "Resources")
            .flatMap(resourceDir -> Optional.ofNullable(resourceDir.findChild("public")));
    }

    private Optional<VirtualFile> getFirstAncestorByName(VirtualFile file, String name) {
        while(file != null && !file.getName().equals(name)) {
            file = file.getParent();
        }

        return Optional.ofNullable(file);
    }
}
