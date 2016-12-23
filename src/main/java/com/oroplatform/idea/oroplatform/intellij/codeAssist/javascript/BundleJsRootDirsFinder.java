package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RootDirsFinder;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

class BundleJsRootDirsFinder implements RootDirsFinder {

    public Collection<VirtualFile> getRootDirs(PsiElement element) {
        return getOroUIBundleClass(element.getProject()).stream()
            .flatMap(phpClass -> toStream(phpClass.getContainingFile().getVirtualFile().getParent()))
            .flatMap(file -> toStream(VfsUtil.findRelativeFile(file, "Resources", "public", "js")))
            .collect(Collectors.toList());
    }

    private Collection<PhpClass> getOroUIBundleClass(Project project) {
        final PhpIndex phpIndex = PhpIndex.getInstance(project);

        return phpIndex.getAllSubclasses("\\Symfony\\Component\\HttpKernel\\Bundle\\Bundle");
    }
}
