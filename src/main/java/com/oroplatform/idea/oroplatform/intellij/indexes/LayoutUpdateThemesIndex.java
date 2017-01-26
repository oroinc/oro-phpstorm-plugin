package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.yaml.YAMLFileType;

import java.util.Collection;

public class LayoutUpdateThemesIndex {
    private final GlobalSearchScope scope;

    private LayoutUpdateThemesIndex(Project project) {
        this.scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(project), YAMLFileType.YML);
    }

    public static LayoutUpdateThemesIndex instance(Project project) {
        return new LayoutUpdateThemesIndex(project);
    }

    public Collection<VirtualFile> findLayoutUpdates(String theme) {
        return FileBasedIndex.getInstance().getContainingFiles(LayoutUpdateThemesFileBasedIndex.KEY, theme, scope);
    }

}
