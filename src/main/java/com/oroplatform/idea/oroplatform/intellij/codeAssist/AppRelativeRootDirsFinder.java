package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class AppRelativeRootDirsFinder implements RootDirsFinder {
    private final String relativeToAppDir;

    public AppRelativeRootDirsFinder(@NotNull String relativeToAppDir) {
        this.relativeToAppDir = relativeToAppDir;
    }

    @Override
    public Collection<VirtualFile> getRootDirs(PsiElement element) {
        final Project project = element.getProject();
        final VirtualFile appDir = OroPlatformSettings.getInstance(project).getAppVirtualDir();

        return Optional.ofNullable(VfsUtil.findRelativeFile(relativeToAppDir, appDir))
            .map(Arrays::asList).orElseGet(Collections::emptyList);
    }
}
