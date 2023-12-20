package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import gnu.trove.THashMap;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Map;

public class RequireJsComponent implements ProjectActivity {
    private static final String REQUIREJS = "requirejs.yml";

    private final Project project;
    private final RequireJsConfigParser configParser = new RequireJsConfigParser();
    private final Map<String, RequireJsConfig> configs = new THashMap<>();
    private final VirtualFileListener vfsListener = new RequireJsConfigListener();

    private RequireJsConfig mergedConfig;

    public RequireJsComponent(Project project) {
        this.project = project;
    }

    private void initConfigs() {
        //OPP-75: Method is not used/called anywhere, calls deprecated methods, blocking for now
//        final PsiFile[] files = Stream.of(
//                        FilenameIndex.getVirtualFilesByName(REQUIREJS, GlobalSearchScope.allScope(project))
//                )
//                .map(virtualFiles -> PsiUtilCore.toPsiFiles(psiManager, virtualFiles))
//                .toArray(PsiFile[]::new);
//        //FilenameIndex.getFilesByName(project, REQUIREJS, GlobalSearchScope.allScope(project));
//        for (PsiFile file : files) {
//            parseConfigFile(file);
//        }
    }

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        configs.clear();
        mergedConfig = null;

        // OPP-75: Blocked as it calls deprecated method but remains for future reference
        // VirtualFileManager.getInstance().removeVirtualFileListener(vfsListener);
        return continuation;
    }

    public void initComponent() {
        // RequireJS bundle was removed in OroCommerce 4.1
        return;
//        if(ApplicationManager.getApplication().isUnitTestMode()) {
//            init();
//        } else {
//            StartupManager.getInstance(project).registerPostStartupActivity(() -> {
//                if(OroPlatformSettings.getInstance(project).isPluginEnabled()) {
//                    init();
//                }
//            });
//        }
    }

    private void init() {
        //OPP-75: This method is not used and calls a deprecated method, blocking for now
//        ApplicationManager.getApplication().runReadAction(() -> {
//            VirtualFileManager.getInstance().addVirtualFileListener(vfsListener, project);
//            initConfigs();
//        });
    }

    @NotNull
    public String getComponentName() {
        return "RequireJsComponent";
    }

    public RequireJsConfig getRequireJsConfig() {
        if (mergedConfig == null) {
            mergedConfig = configs.values().stream().reduce(new RequireJsConfig(), (config1, config2) -> config1.merge(config2));
        }

        return mergedConfig;
    }

    private class RequireJsConfigListener implements VirtualFileListener {
        @Override
        public void contentsChanged(@NotNull VirtualFileEvent event) {
            if (event.getFileName().equals(REQUIREJS)) {
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
        if (file instanceof YAMLFile) {
            configs.put(file.getVirtualFile().getPath(), configParser.parse((YAMLFile) file));
        }
    }
}
