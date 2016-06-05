package com.oroplatform.idea.oroplatform.schema;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.oroplatform.idea.oroplatform.intellij.indexes.Cache;
import com.oroplatform.idea.oroplatform.intellij.indexes.ImportIndex;
import org.jetbrains.yaml.YAMLFileType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class WorkflowMatcher implements FileMatcher {

    private static final Key<CachedValue<Collection<String>>> IMPORT_CACHE_KEY = new Key<CachedValue<Collection<String>>>("com.oroplatform.idea.oroplatform.cache.import_index");

    @Override
    public boolean matches(PsiFile file) {
        if(file.getOriginalFile().getVirtualFile().getPath().endsWith(Schemas.FilePathPatterns.WORKFLOW)) {
            return true;
        }

        final GlobalSearchScope scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(file.getProject()), YAMLFileType.YML);
        final PsiFile[] workflows = FilenameIndex.getFilesByName(file.getProject(), "workflow.yml", scope);

        for (PsiFile workflow : workflows) {
            if(filePath(workflow).endsWith(Schemas.FilePathPatterns.WORKFLOW)) {
                if (isImported(file, workflow, scope)) return true;
            }
        }

        return false;
    }

    private static String filePath(PsiFile file) {
        return file.getOriginalFile().getVirtualFile().getPath();
    }

    private boolean isImported(PsiFile importedFile, PsiFile importingFile, GlobalSearchScope scope) {
        for (String nextImportedFilePath : getImportedFilePaths(importingFile, scope)) {
            if(nextImportedFilePath.equals(importedFile.getOriginalFile().getVirtualFile().getPath())) {
                return true;
            }

            final VirtualFileSystem fileSystem = importedFile.getOriginalFile().getVirtualFile().getFileSystem();
            final VirtualFile nextImportedFile = fileSystem.findFileByPath(nextImportedFilePath);

            if (nextImportedFile != null) {
                PsiFile importedPsiFile = PsiManager.getInstance(importedFile.getProject()).findFile(nextImportedFile);
                if (importedPsiFile != null && isImported(importedFile, importedPsiFile, scope)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Collection<String> getImportedFilePaths(PsiFile file, GlobalSearchScope scope) {
        List<String> paths = new LinkedList<String>();

        for (String path : Cache.getSet(scope.getProject(), file, IMPORT_CACHE_KEY, ImportIndex.KEY, filePath(file), scope)) {
            paths.add(path);
        }

        return paths;
    }
}
