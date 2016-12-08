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

import java.util.stream.Stream;

class WorkflowMatcher implements FileMatcher {

    private final String rootFileName;

    WorkflowMatcher(String rootFileName) {
        this.rootFileName = rootFileName;
    }

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

        if(isWorkflowPath(file.getOriginalFile().getVirtualFile().getPath())) {
            return true;
        }

        final GlobalSearchScope scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(file.getProject()), YAMLFileType.YML);
        final ImportIndex index = ImportIndex.instance(file.getProject());

        return Stream.of(rootFileName)
            .flatMap(filename -> Stream.of(FilenameIndex.getFilesByName(file.getProject(), filename, scope)))
            .anyMatch(workflowFile -> isWorkflowPath(filePath(workflowFile)) && isImported(index, file, workflowFile));
    }

    private boolean isWorkflowPath(String path) {
        return (path.endsWith(SchemasV1.FilePathPatterns.WORKFLOW) || path.endsWith(SchemasV2.FilePathPatterns.WORKFLOW)) && path.endsWith(rootFileName);
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
                final PsiFile importedPsiFile = PsiManager.getInstance(importedFile.getProject()).findFile(nextImportedFile);
                if (importedPsiFile != null && isImported(index, importedFile, importedPsiFile)) {
                    return true;
                }
            }
        }
        return false;
    }

}
