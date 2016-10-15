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

public class PublicResourceWrappedStringFactory implements StringWrapperProvider {
    @Override
    public StringWrapper getStringWrapperFor(PsiElement element) {
        final VirtualFile publicDir = new PublicResourcesRootDirFinder().getRootDir(element);

        if(publicDir == null) return new StringWrapper("", "");

        final VirtualFile bundleDir = publicDir.getParent().getParent();
        final VirtualFile phpFile = getPhpFile(bundleDir);
        if (phpFile == null) return new StringWrapper("", "");

        final PsiFile file = PsiManager.getInstance(element.getProject()).findFile(phpFile);

        if(file == null) return new StringWrapper("", "");

        for (PsiElement e : file.getChildren()) {
            for (PhpNamespace phpNamespace : PsiTreeUtil.getChildrenOfTypeAsList(e, PhpNamespace.class)) {
                final Bundle bundle = new Bundle(phpNamespace.getFQN());
                return new StringWrapper("bundles/"+bundle.getResourceName()+"/", "");
            }
        }

        return new StringWrapper("", "");
    }

    @Nullable
    private VirtualFile getPhpFile(VirtualFile bundleDir) {
        for (VirtualFile virtualFile : bundleDir.getChildren()) {
            if(virtualFile.getName().endsWith(".php")) {
                return virtualFile;
            }
        }
        return null;
    }
}
