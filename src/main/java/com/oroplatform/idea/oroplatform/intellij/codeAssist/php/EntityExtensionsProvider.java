package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;

import java.util.regex.Pattern;

public class EntityExtensionsProvider {

    private static EntityExtensionsProvider instance = null;
    private final Project project;

    private static final Pattern EXTENSION_FILE_PATTERN = Pattern.compile("^EX_([a-z-A-Z0-9][a-z-A-Z0-9_]*)\\.php$");

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
        PhpBasedEntityExtensions phpBasedEntityExtensions = getExFileEntityExtensions(appDir);

        if (phpBasedEntityExtensions != null) {
            return phpBasedEntityExtensions;
        }
        return null;
    }

    private PhpBasedEntityExtensions getExFileEntityExtensions(VirtualFile appDir) {
        final VirtualFile extensionsDir = appDir.findFileByRelativePath(EntityExtensions.EXTENSIONS_DIR_RELATIVE_PATH);
        if (extensionsDir.getChildren() == null) {
            return null;
        }
        for (VirtualFile file: extensionsDir.getChildren()) {
            if (EXTENSION_FILE_PATTERN.matcher(file.getName()).matches()) {
                return PhpBasedEntityExtensions.instance(project); //TODO rewrite from pseudo-singleton
            }
        }

        return null;
    }

    private VirtualFile getAppDir(Project project) {
        final OroPlatformSettings settings = OroPlatformSettings.getInstance(project);
        return settings.getAppVirtualDir();
    }
}
