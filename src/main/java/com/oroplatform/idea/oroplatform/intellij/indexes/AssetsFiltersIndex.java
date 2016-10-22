package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;

import java.util.Collection;
import java.util.Collections;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getMappingsFrom;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getPropertyFrom;

public class AssetsFiltersIndex {
    private static final Key<CachedValue<Collection<String>>> CACHE_KEY =
        new Key<>("com.oroplatform.idea.oroplatform.cache.assets_filter");

    private final Project project;

    private AssetsFiltersIndex(Project project) {
        this.project = project;
    }

    public static AssetsFiltersIndex instance(Project project) {
        return new AssetsFiltersIndex(project);
    }

    public Collection<String> getFilters() {
        final OroPlatformSettings settings = OroPlatformSettings.getInstance(project);
        final VirtualFile appDir = settings.getAppVirtualDir();

        if(appDir == null || VfsUtil.findRelativeFile(appDir, "config", "config.yml") == null) return Collections.emptyList();

        CachedValue<Collection<String>> cachedValue = project.getUserData(CACHE_KEY);

        if(cachedValue == null) {
            cachedValue = CachedValuesManager.getManager(project).createCachedValue(() -> {
                final VirtualFile appDir1 = settings.getAppVirtualDir(); // up to date appDir
                final VirtualFile configFile = VfsUtil.findRelativeFile(appDir1, "config", "config.yml");
                return CachedValueProvider.Result.create(getFiltersFrom(configFile), settings, configFile);
            }, false);

            project.putUserData(CACHE_KEY, cachedValue);
        }

        return cachedValue.getValue();
    }

    private Collection<String> getFiltersFrom(VirtualFile configFile) {
        if(configFile == null) return Collections.emptyList();

        final PsiFile file = PsiManager.getInstance(project).findFile(configFile);

        if(file == null) return Collections.emptyList();

        return getPropertyFrom(new PropertyPath("assetic", "filters").pointsToValue(), getMappingsFrom(file), Collections.emptySet());
    }
}
