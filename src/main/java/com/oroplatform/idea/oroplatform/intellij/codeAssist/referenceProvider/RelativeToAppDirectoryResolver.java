package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RelativeToAppDirectoryResolver implements RelativeDirectoryResolver {
    private final String relativeToAppDir;

    public RelativeToAppDirectoryResolver(@NotNull String relativeToAppDir) {
        this.relativeToAppDir = relativeToAppDir;
    }

    @Override
    public Optional<PsiDirectory> resolve(PsiElement element) {
        final Project project = element.getProject();
        final VirtualFile appDir = OroPlatformSettings.getInstance(project).getAppVirtualDir();

        return Optional.ofNullable(VfsUtil.findRelativeFile(relativeToAppDir, appDir))
            .flatMap(rootDir -> Optional.ofNullable(PsiManager.getInstance(project).findDirectory(rootDir)));
    }
}
