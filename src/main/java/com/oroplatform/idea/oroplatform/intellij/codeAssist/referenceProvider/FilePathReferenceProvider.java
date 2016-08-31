package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilePathReferenceProvider extends PsiReferenceProvider {
    private final String relativeToAppDir;

    public FilePathReferenceProvider(String relativeToAppDir) {
        this.relativeToAppDir = relativeToAppDir;
    }

    public FilePathReferenceProvider() {
        this(null);
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return (relativeToAppDir == null ? new FileReferenceSet(element) : new RelativeFileReferenceSet(element, relativeToAppDir))
            .getAllReferences();
    }

    private static class RelativeFileReferenceSet extends FileReferenceSet {
        private final String relativeToAppDir;

        RelativeFileReferenceSet(PsiElement element, String relativeToAppDir) {
            super(element);
            this.relativeToAppDir = relativeToAppDir;
        }

        @Nullable
        @Override
        protected PsiFile getContainingFile() {
            final Project project = getElement().getProject();
            final VirtualFile dir = project.getBaseDir();
            final VirtualFile appDir = OroPlatformSettings.getInstance(project).getAppDir().isEmpty() ?
                dir : VfsUtil.findRelativeFile(dir, OroPlatformSettings.getInstance(project).getAppDir());

            if(relativeToAppDir != null && appDir != null) {
                final VirtualFile rootDir = VfsUtil.findRelativeFile(relativeToAppDir, appDir);
                if(rootDir != null) {
                    final PsiDirectory psiRootDir = PsiManager.getInstance(project).findDirectory(rootDir);
                    if(psiRootDir != null && psiRootDir.getFiles().length > 0) {
                        return psiRootDir.getFiles()[0];
                    }
                }
            }

            return super.getContainingFile();
        }
    }
}
