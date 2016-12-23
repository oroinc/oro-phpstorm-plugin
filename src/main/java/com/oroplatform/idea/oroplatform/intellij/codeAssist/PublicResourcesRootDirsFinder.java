package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class PublicResourcesRootDirsFinder implements RootDirsFinder {
    @Override
    public Collection<VirtualFile> getRootDirs(PsiElement element) {
        return getFirstAncestorByName(containingFile(element), "Resources")
            .flatMap(resourceDir -> Optional.ofNullable(resourceDir.findChild("public")))
            .map(Arrays::asList).orElseGet(Collections::emptyList);
    }

    private static VirtualFile containingFile(PsiElement element) {
        return element instanceof PsiDirectory ?
            ((PsiDirectory) element).getVirtualFile() : element.getContainingFile().getOriginalFile().getVirtualFile();
    }

    private Optional<VirtualFile> getFirstAncestorByName(VirtualFile file, String name) {
        while(file != null && !file.getName().equals(name)) {
            file = file.getParent();
        }

        return Optional.ofNullable(file);
    }
}
