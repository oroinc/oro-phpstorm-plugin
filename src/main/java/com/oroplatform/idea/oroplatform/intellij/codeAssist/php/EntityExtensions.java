package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Entity;
import gnu.trove.THashMap;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class EntityExtensions {

    static final String EXTENSIONS_DIR_RELATIVE_PATH = "var/cache/dev/oro_entities/Extend/Entity";

    private static final Key<CachedValue<Map<String, Collection<String>>>> CACHE_KEY =
        new Key<>("com.oroplatform.idea.oroplatform.cache.entity_extensions");

    private final Project project;
    private static final Pattern METHOD_PATTERN = Pattern.compile("public function ([a-z-A-Z0-9][a-z-A-Z0-9_]*)");

    private EntityExtensions(Project project) {
        this.project = project;
    }

    public static EntityExtensions instance(Project project) {
        return new EntityExtensions(project);
    }

    Collection<ExtensionMethod> getMethods(Entity entity) {
        final String filename = "EX_" + entity.getBundle().getName() + "_" + entity.getSimpleName()+".php";

        final Collection<String> methodNames = getEntityExtensions().get(filename);

        if(methodNames == null) return Collections.emptyList();

        return methodNames.stream().map(ExtensionMethod::new).collect(Collectors.toList());
    }

    private Map<String, Collection<String>> getEntityExtensions() {
        CachedValue<Map<String, Collection<String>>> cachedExtensions = project.getUserData(CACHE_KEY);

        if(cachedExtensions == null) {
            cachedExtensions = CachedValuesManager.getManager(project).createCachedValue(() -> {
                final OroPlatformSettings settings = OroPlatformSettings.getInstance(project);
                final VirtualFile appDir = settings.getAppVirtualDir();

                if(appDir == null) return null;
                final VirtualFile extensionsDir = appDir.findFileByRelativePath(EXTENSIONS_DIR_RELATIVE_PATH);
                if(extensionsDir == null) return null;

                final Map<String, Collection<String>> extensions = loadEntityExtensions(extensionsDir);
                final Collection<Object> dependencies = new LinkedList<>();
                dependencies.add(settings);
                dependencies.add(extensionsDir);
                dependencies.addAll(Arrays.asList(extensionsDir.getChildren()));

                return CachedValueProvider.Result.create(extensions, dependencies);
            }, false);

            project.putUserData(CACHE_KEY, cachedExtensions);
        }

        final Map<String, Collection<String>> value = cachedExtensions.getValue();
        return value == null ? Collections.emptyMap() : value;
    }

    private Map<String, Collection<String>> loadEntityExtensions(VirtualFile extensionsDir) {
        final Map<String, Collection<String>> extensions = new THashMap<>();
        for (VirtualFile file : extensionsDir.getChildren()) {
            extensions.put(file.getName(), getExtensionMethodsFromFile(file));
        }

        return extensions;
    }

    private Collection<String> getExtensionMethodsFromFile(VirtualFile classFile) {
        try {
            final String contents = new String(classFile.contentsToByteArray(), classFile.getCharset());
            final Collection<String> methods = new LinkedList<>();
            final Matcher matcher = METHOD_PATTERN.matcher(contents);
            while(matcher.find()) {
                methods.add(matcher.group(1));
            }
            return methods;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    static class ExtensionMethod {
        final String name;

        ExtensionMethod(String name) {
            this.name = name;
        }
    }
}
