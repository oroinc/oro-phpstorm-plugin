package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AppRelativeRootDirFinder implements RootDirFinder {
    private final String relativeToAppDir;

    public AppRelativeRootDirFinder(@NotNull String relativeToAppDir) {
        this.relativeToAppDir = relativeToAppDir;
    }

    @Override
    public Optional<VirtualFile> getRootDir(PsiElement element) {
        final Project project = element.getProject();
        final VirtualFile appDir = OroPlatformSettings.getInstance(project).getAppVirtualDir();

        return Optional.ofNullable(VfsUtil.findRelativeFile(relativeToAppDir, appDir));
    }
}
