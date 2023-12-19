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

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

class ImportedFileMatcher implements FileMatcher {

    private final String rootFileName;
    private final Collection<String> rootFilePatterns;

    ImportedFileMatcher(String rootFileName, String... rootFilePatterns) {
        this.rootFileName = rootFileName;
        this.rootFilePatterns = Arrays.asList(rootFilePatterns);
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

        if(isRootFile(file.getOriginalFile().getVirtualFile().getPath())) {
            return true;
        }

        final GlobalSearchScope scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(file.getProject()), YAMLFileType.YML);
        final ImportIndex index = ImportIndex.instance(file.getProject());

        return Stream.of(rootFileName)
            .flatMap(filename -> Stream.of(FilenameIndex.getVirtualFilesByName(filename, scope).toArray(VirtualFile[]::new)))
            .anyMatch(rootFile -> isRootFile(filePath(rootFile)) && isImported(index, file, rootFile));
    }

    private boolean isRootFile(String path) {
        return rootFilePatterns.stream().anyMatch(path::endsWith) && path.endsWith(rootFileName);
    }

    private static String filePath(VirtualFile file) {
        return file.getPath();
    }

    private boolean isImported(ImportIndex index, PsiFile importedFile, VirtualFile importingFile) {
        for (String nextImportedFilePath : index.getImportedFilePathsFor(importingFile)) {
            if(nextImportedFilePath.equals(importedFile.getOriginalFile().getVirtualFile().getPath())) {
                return true;
            }

            final VirtualFileSystem fileSystem = importedFile.getOriginalFile().getVirtualFile().getFileSystem();
            final VirtualFile nextImportedFile = fileSystem.findFileByPath(nextImportedFilePath);

            if (nextImportedFile != null) {
                final PsiFile importedPsiFile = PsiManager.getInstance(importedFile.getProject()).findFile(nextImportedFile);
                if (importedPsiFile != null && isImported(index, importedFile, importedPsiFile.getVirtualFile())) {
                    return true;
                }
            }
        }
        return false;
    }

}
