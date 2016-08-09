package com.oroplatform.idea.oroplatform.schema;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.oroplatform.idea.oroplatform.intellij.indexes.ImportIndex;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.yaml.YAMLFileType;

class WorkflowMatcher implements FileMatcher {

    @Override
    public boolean matches(PsiFile file) {
        if(!OroPlatformSettings.getInstance(file.getProject()).isPluginEnabled()) {
            //avoid caching empty indexes values when plugin is disabled
            return false;
        }

        if(ProgressManager.getInstance().hasModalProgressIndicator()) {
            //fix for: https://github.com/orocrm/oro-phpstorm-plugin/issues/5
            //refactoring or other heavy stuff is in progress, skip in order to avoid indexing
            return false;
        }

        if(file.getOriginalFile().getVirtualFile().getPath().endsWith(Schemas.FilePathPatterns.WORKFLOW)) {
            return true;
        }

        final GlobalSearchScope scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(file.getProject()), YAMLFileType.YML);
        final PsiFile[] workflows = FilenameIndex.getFilesByName(file.getProject(), "workflow.yml", scope);
        final ImportIndex index = ImportIndex.instance(file.getProject());

        for (PsiFile workflow : workflows) {
            if(filePath(workflow).endsWith(Schemas.FilePathPatterns.WORKFLOW)) {
                if (isImported(index, file, workflow)) return true;
            }
        }

        return false;
    }

    private static String filePath(PsiFile file) {
        return file.getOriginalFile().getVirtualFile().getPath();
    }

    private boolean isImported(ImportIndex index, PsiFile importedFile, PsiFile importingFile) {
        for (String nextImportedFilePath : index.getImportedFilePathsFor(importingFile)) {
            if(nextImportedFilePath.equals(importedFile.getOriginalFile().getVirtualFile().getPath())) {
                return true;
            }

            final VirtualFileSystem fileSystem = importedFile.getOriginalFile().getVirtualFile().getFileSystem();
            final VirtualFile nextImportedFile = fileSystem.findFileByPath(nextImportedFilePath);

            if (nextImportedFile != null) {
                PsiFile importedPsiFile = PsiManager.getInstance(importedFile.getProject()).findFile(nextImportedFile);
                if (importedPsiFile != null && isImported(index, importedFile, importedPsiFile)) {
                    return true;
                }
            }
        }
        return false;
    }

}
