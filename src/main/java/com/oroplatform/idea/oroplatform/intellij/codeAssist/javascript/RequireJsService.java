package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilCore;
import gnu.trove.THashMap;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Collection;
import java.util.Map;

public final class RequireJsService implements RequireJsInterface {
    private static final String JSCONFIG = "jsmodules.yml";

    private final Project project;
    private final RequireJsConfigParser configParser = new RequireJsConfigParser();
    private final Map<String, RequireJsConfig> configs = new THashMap<>();
    private RequireJsConfig mergedConfig;

    private static RequireJsService instance = null;

    public static RequireJsService getInstance(Project project) {
        if (instance == null) {
            instance = new RequireJsService(project);
        }

        return instance;
    }

    private RequireJsService(Project project) {
        this.project = project;
        initConfigs();
    }

    @Override
    public RequireJsConfig getRequireJsConfig() {
        if (mergedConfig == null) {
            mergedConfig = configs.values().stream().reduce(new RequireJsConfig(), (config1, config2) -> config1.merge(config2));
        }

        return mergedConfig;
    }

    private void initConfigs() {
        // OPP-96: the original stream-based implementation was completely non-functional
        Collection<VirtualFile> virtualFiles = FilenameIndex
                .getVirtualFilesByName(JSCONFIG, GlobalSearchScope.allScope(project));
        final PsiFile[] files =
                PsiUtilCore.toPsiFiles(project.getService(PsiManager.class), virtualFiles).toArray(new PsiFile[0]);

        for (PsiFile file : files) {
            parseConfigFile(file);
        }
    }

    private void parseConfigFile(PsiFile file) {
        if (file instanceof YAMLFile) {
            configs.put(file.getVirtualFile().getPath(), configParser.parse((YAMLFile) file));
        }
    }
}
