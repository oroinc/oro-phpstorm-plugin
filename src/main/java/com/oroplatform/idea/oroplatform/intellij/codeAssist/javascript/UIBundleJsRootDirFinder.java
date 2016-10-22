package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RootDirFinder;

import java.util.Optional;

class UIBundleJsRootDirFinder implements RootDirFinder {

    public Optional<VirtualFile> getRootDir(PsiElement element) {
        return getOroUIBundleClass(element.getProject())
            .flatMap(uiClass -> Optional.ofNullable(uiClass.getContainingFile().getVirtualFile().getParent()))
            .flatMap(file -> Optional.ofNullable(VfsUtil.findRelativeFile(file, "Resources", "public", "js")));
    }

    private Optional<PhpClass> getOroUIBundleClass(Project project) {
        final PhpIndex phpIndex = PhpIndex.getInstance(project);

        return phpIndex.getClassesByFQN("\\Oro\\Bundle\\UIBundle\\OroUIBundle").stream()
            .findFirst();
    }
}
