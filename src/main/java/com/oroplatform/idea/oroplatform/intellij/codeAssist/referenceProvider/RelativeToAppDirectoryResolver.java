package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

public class RelativeToAppDirectoryResolver implements RelativeDirectoryResolver {
    private final String relativeToAppDir;

    public RelativeToAppDirectoryResolver(@NotNull String relativeToAppDir) {
        this.relativeToAppDir = relativeToAppDir;
    }

    @Override
    public PsiDirectory resolve(PsiElement element) {
        final Project project = element.getProject();
        final VirtualFile dir = project.getBaseDir();
        final VirtualFile appDir = OroPlatformSettings.getInstance(project).getAppDir().isEmpty() ?
            dir : VfsUtil.findRelativeFile(dir, OroPlatformSettings.getInstance(project).getAppDir());

        final VirtualFile rootDir = VfsUtil.findRelativeFile(relativeToAppDir, appDir);
        if(rootDir != null) {
            return PsiManager.getInstance(project).findDirectory(rootDir);
        }

        return null;
    }
}
