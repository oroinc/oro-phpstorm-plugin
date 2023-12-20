package com.oroplatform.idea.oroplatform.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.vfs.VfsUtil;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectConfigurator implements ProjectActivity {

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        if(couldPluginBeEnabled(project)) {
            OroNotifications.showPluginEnableNotification(project);
        }
        return continuation;
    }

    private boolean couldPluginBeEnabled(@NotNull Project project) {
        return isOroPlatformDetected(project) && OroPlatformSettings.getInstance(project).couldPluginBeEnabled();
    }

    private boolean isOroPlatformDetected(@NotNull Project project) {
        return VfsUtil.findRelativeFile(ProjectUtil.guessProjectDir(project), "vendor", "oro", "platform") != null ||
            VfsUtil.findRelativeFile(ProjectUtil.guessProjectDir(project), "package", "platform", "src", "Oro") != null;
    }
}
