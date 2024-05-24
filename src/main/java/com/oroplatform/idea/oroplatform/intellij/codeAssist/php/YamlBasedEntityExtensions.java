package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Entity;
import com.oroplatform.idea.oroplatform.util.OroPlatformStringUtil;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.*;
import java.util.stream.Collectors;

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

        VirtualFile appDir = getAppDir(project);
        VirtualFile extensionsDir = appDir.findFileByRelativePath(EntityExtensions.EXTENSIONS_DIR_RELATIVE_PATH);
        if (extensionsDir == null) {
            return null;
        }
        VirtualFile entityFile = extensionsDir.findFileByRelativePath(entityFilename);

        if (entityFile == null) {
            return null;
        }

        YAMLFile yamlFile = (YAMLFile) PsiManager.getInstance(project).findFile(entityFile);

        return parseYamlFile(yamlFile, entityKey);
    }

    private Collection<ExtensionMethod> parseYamlFile(YAMLFile yamlFile, String entityKey) {
        YAMLKeyValue root = YAMLUtil.getQualifiedKeyInFile(yamlFile, entityKey);
        if (root == null) {
            return null;
        }
        try {
            List<ExtensionMethod> setters = getMethods(root, "set");
            List<ExtensionMethod> getters = getMethods(root, "get");
            setters.addAll(getters);
            return setters;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }

    }

    private List<ExtensionMethod> getMethods(YAMLKeyValue root, String prefix) {
        return Arrays.stream(root.getChildren())
                .map(child -> (YAMLMapping) child)
                .map(child -> child.getKeyValueByKey("fields")).filter(Objects::nonNull)
                .map(fields -> (YAMLMapping) fields.getValue()).filter(Objects::nonNull)
                .flatMap(fields -> extractMethods(fields, prefix).stream())
                .collect(Collectors.toList());
    }

    private Collection<ExtensionMethod> extractMethods(YAMLMapping fields, String prefix) {
        return Arrays.stream(fields.getChildren())
                .map(child -> ((YAMLKeyValue) child).getKeyText())
                .map(OroPlatformStringUtil::snakeToCamel)
                .map(field -> new ExtensionMethod(prefix + OroPlatformStringUtil.capitalizeFirstCharacter(field)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private VirtualFile getAppDir(Project project) {
        final OroPlatformSettings settings = OroPlatformSettings.getInstance(project);
        return settings.getAppVirtualDir();
    }


}
