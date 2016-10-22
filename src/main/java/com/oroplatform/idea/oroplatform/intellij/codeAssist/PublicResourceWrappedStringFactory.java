package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.oroplatform.idea.oroplatform.StringWrapper;
import com.oroplatform.idea.oroplatform.symfony.Bundle;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public class PublicResourceWrappedStringFactory implements StringWrapperProvider {
    @Override
    public StringWrapper getStringWrapperFor(PsiElement element) {
        return new PublicResourcesRootDirFinder().getRootDir(element)
            .map(publicDir -> publicDir.getParent().getParent())
            .flatMap(this::findFirstPhpFile)
            .flatMap(phpFile -> getPsiFile(element, phpFile))
            .flatMap(psiFile ->
                Stream.of(psiFile.getChildren())
                    .flatMap(childFile -> PsiTreeUtil.getChildrenOfTypeAsList(childFile, PhpNamespace.class).stream())
                    .map(phpNamespace -> new Bundle(phpNamespace.getFQN()))
                    .map(bundle -> new StringWrapper("bundles/"+bundle.getResourceName()+"/", ""))
                    .findFirst()
            ).orElseGet(() -> new StringWrapper("", ""));
    }

    private Optional<PsiFile> getPsiFile(PsiElement element, VirtualFile phpFile) {
        return Optional.ofNullable(PsiManager.getInstance(element.getProject()).findFile(phpFile));
    }

    @Nullable
    private Optional<VirtualFile> findFirstPhpFile(VirtualFile bundleDir) {
        for (VirtualFile virtualFile : bundleDir.getChildren()) {
            if(virtualFile.getName().endsWith(".php")) {
                return Optional.of(virtualFile);
            }
        }
        return Optional.empty();
    }
}
