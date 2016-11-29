package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.HashMap;
import java.util.Map;

public class RequireJsComponent implements ProjectComponent {
    private static final String REQUIREJS = "requirejs.yml";

    private final Project project;
    private final RequireJsConfigParser configParser = new RequireJsConfigParser();
    private final Map<String, RequireJsConfig> configs = new HashMap<>();
    private final VirtualFileListener vfsListener = new RequireJsConfigListener();

    private RequireJsConfig mergedConfig;

    public RequireJsComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {

    }

    private void initConfigs() {
        final PsiFile[] files = FilenameIndex.getFilesByName(project, REQUIREJS, GlobalSearchScope.allScope(project));
        for (PsiFile file : files) {
            parseConfigFile(file);
        }
    }

    @Override
    public void projectClosed() {
        configs.clear();
        mergedConfig = null;
        VirtualFileManager.getInstance().removeVirtualFileListener(vfsListener);
    }

    @Override
    public void initComponent() {
        StartupManager.getInstance(project).registerPostStartupActivity(() -> {
            if(OroPlatformSettings.getInstance(project).isPluginEnabled()) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    VirtualFileManager.getInstance().addVirtualFileListener(vfsListener, project);
                    initConfigs();
                });
            }
        });
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "RequireJsComponent";
    }

    public RequireJsConfig getRequireJsConfig() {
        if(mergedConfig == null) {
            mergedConfig = configs.values().stream().reduce(new RequireJsConfig(), (config1, config2) -> config1.merge(config2));
        }

        return mergedConfig;
    }

    private class RequireJsConfigListener extends VirtualFileAdapter {
        @Override
        public void contentsChanged(@NotNull VirtualFileEvent event) {
            if(event.getFileName().equals(REQUIREJS)) {
                final PsiFile file = PsiManager.getInstance(project).findFile(event.getFile());
                parseConfigFile(file);
            }
        }

        @Override
        public void fileDeleted(@NotNull VirtualFileEvent event) {
            if(event.getFileName().equals(REQUIREJS)) {
                configs.remove(event.getFile().getPath());
            }
        }
    }

    private void parseConfigFile(PsiFile file) {
        if(file instanceof YAMLFile) {
            configs.put(file.getVirtualFile().getPath(), configParser.parse((YAMLFile) file));
        }
    }
}
