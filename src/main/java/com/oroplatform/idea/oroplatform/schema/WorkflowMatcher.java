package com.oroplatform.idea.oroplatform.schema;

import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.oroplatform.idea.oroplatform.intellij.indexes.ImportIndex;
import org.jetbrains.yaml.YAMLFileType;

import java.util.Collection;

class WorkflowMatcher implements FileMatcher {

    @Override
    public boolean matches(PsiFile file) {
        if(file.getOriginalFile().getVirtualFile().getPath().endsWith(Schemas.FilePathPatterns.WORKFLOW)) {
            return true;
        }

        final GlobalSearchScope scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(file.getProject()), YAMLFileType.YML);
        final PsiFile[] workflows = FilenameIndex.getFilesByName(file.getProject(), "workflow.yml", scope);

        for (PsiFile workflow : workflows) {
            final String workflowPath = workflow.getOriginalFile().getVirtualFile().getPath();
            if(workflowPath.endsWith(Schemas.FilePathPatterns.WORKFLOW)) {
                if (isImported(file, workflowPath, scope)) return true;
            }
        }

        return false;
    }

    private boolean isImported(PsiFile file, String rootFilePath, GlobalSearchScope scope) {
        for (Collection<String> importedFilePaths : FileBasedIndex.getInstance().getValues(ImportIndex.KEY, rootFilePath, scope)) {
            for (String importedFilePath : importedFilePaths) {
                if(importedFilePath.equals(file.getOriginalFile().getVirtualFile().getPath())) {
                    return true;
                }

                if(isImported(file, importedFilePath, scope)) {
                    return true;
                }
            }
        }
        return false;
    }
}
