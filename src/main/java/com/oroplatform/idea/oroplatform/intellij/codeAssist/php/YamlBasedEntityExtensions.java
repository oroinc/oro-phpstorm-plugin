package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.oroplatform.idea.oroplatform.symfony.Entity;

import java.util.Collection;

public class YamlBasedEntityExtensions implements EntityExtensions {

    private static YamlBasedEntityExtensions instance = null;

    private Project project;

    private YamlBasedEntityExtensions(Project project) {
        this.project = project;
    }

    public static YamlBasedEntityExtensions instance(Project project) {
        if (instance == null) {
            instance = new YamlBasedEntityExtensions(project);
        }
        return instance;
    }

    @Override
    public Collection<ExtensionMethod> getMethods(Entity entity) {
        String entityName = "EX_" + entity.getBundle().getName() + "_" + entity.getSimpleName();
        String entityFilename = entityName + ".orm.yml";
        String entityKey = "Extend\\Entity\\" + entityName;

        Collection<VirtualFile> virtualFiles = FilenameIndex
                .getVirtualFilesByName(entityFilename, GlobalSearchScope.allScope(project));

        return null;
    }

}
