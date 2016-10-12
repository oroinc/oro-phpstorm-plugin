package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.WrappedFileReferenceProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

class UIBundleJsRootDirFinder implements WrappedFileReferenceProvider.RootDirFinder {
    @Nullable
    public VirtualFile getRootDir(PsiElement element) {

        final PhpClass uiClass = getOroUIBundleClass(element.getProject());

        if(uiClass == null) {
            return null;
        }

        final VirtualFile file = uiClass.getContainingFile().getVirtualFile().getParent();

        return VfsUtil.findRelativeFile(file, "Resources", "public", "js");
    }

    private PhpClass getOroUIBundleClass(Project project) {
        final PhpIndex phpIndex = PhpIndex.getInstance(project);

        final Collection<PhpClass> classes = phpIndex.getClassesByFQN("\\Oro\\Bundle\\UIBundle\\OroUIBundle");

        return classes.isEmpty() ? null : classes.iterator().next();
    }
}
