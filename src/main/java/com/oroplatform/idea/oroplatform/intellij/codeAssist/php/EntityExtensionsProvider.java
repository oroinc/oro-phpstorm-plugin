package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;

import java.util.regex.Pattern;

public class EntityExtensionsProvider {

    private static EntityExtensionsProvider instance = null;
    private final Project project;


    public static EntityExtensionsProvider instance(Project project) {
        if (instance == null) {
            instance = new EntityExtensionsProvider(project);
        }
        return instance;
    }

    private EntityExtensionsProvider(Project project) {
        this.project = project;
    }

    public EntityExtensions getEntityExtensions() {
        VirtualFile appDir = getAppDir(project);
        PhpBasedEntityExtensions phpBasedEntityExtensions = getPhpBasedEntityExtensions(appDir);
        YamlBasedEntityExtensions yamlBasedEntityExtensions = getYamlBasedEntityExtensions(appDir);

        if (phpBasedEntityExtensions != null) {
            return phpBasedEntityExtensions;
        }

        if (yamlBasedEntityExtensions != null) {
            return yamlBasedEntityExtensions;
        }
        return null;
    }

    private PhpBasedEntityExtensions getPhpBasedEntityExtensions(VirtualFile appDir) {
        final VirtualFile extensionsDir = appDir.findFileByRelativePath(EntityExtensions.EXTENSIONS_DIR_RELATIVE_PATH);
        if (extensionsDir != null && extensionsDir.getChildren() == null) {
            return null;
        }
        PhpExtensionFileFinder phpExtensionFileFinder = new PhpExtensionFileFinder();
        if (extensionsDir != null && phpExtensionFileFinder.hasPhpExtensionFile(extensionsDir)) {
            return PhpBasedEntityExtensions.instance(project);
        }

        return null;
    }

    private YamlBasedEntityExtensions getYamlBasedEntityExtensions(VirtualFile appDir) {
        final VirtualFile extensionsDir = appDir.findFileByRelativePath(EntityExtensions.EXTENSIONS_DIR_RELATIVE_PATH);
        if (extensionsDir == null || extensionsDir.getChildren() == null) {
            return null;
        }
        if (new YamlExtensionFileFinder().hasYamlExtensionFile(extensionsDir)) {
            return YamlBasedEntityExtensions.instance(project);
        }
        return null;
    }

    private VirtualFile getAppDir(Project project) {
        final OroPlatformSettings settings = OroPlatformSettings.getInstance(project);
        return settings.getAppVirtualDir();
    }

    private static class PhpExtensionFileFinder {
        private static final Pattern PHP_EXTENSION_FILE_PATTERN = Pattern.compile("^EX_([a-z-A-Z0-9][a-z-A-Z0-9_]*)\\.php$");

        public boolean hasPhpExtensionFile(VirtualFile extensionsDir) {
            if (extensionsDir.getChildren() == null) {
                return false;
            }
            for (VirtualFile file : extensionsDir.getChildren()) {
                if (PHP_EXTENSION_FILE_PATTERN.matcher(file.getName()).matches()) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class YamlExtensionFileFinder {
        private static final Pattern YAML_EXTENSION_FILE_PATTERN = Pattern.compile("^EX_([a-z-A-Z0-9][a-z-A-Z0-9_]*)\\.orm\\.yml$");

        public boolean hasYamlExtensionFile(VirtualFile extensionsDir) {
            if (extensionsDir.getChildren() == null) {
                return false;
            }
            for (VirtualFile file : extensionsDir.getChildren()) {
                if (YAML_EXTENSION_FILE_PATTERN.matcher(file.getName()).matches()) {
                    return true;
                }
            }
            return false;
        }
    }
}
