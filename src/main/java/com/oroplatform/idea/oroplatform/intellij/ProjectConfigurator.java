package com.oroplatform.idea.oroplatform.intellij;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

public class ProjectConfigurator implements ProjectComponent {

    private final Project project;

    public ProjectConfigurator(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        if(couldPluginBeEnabled()) {
            OroNotifications.showPluginEnableNotification(project);
        }
    }

    private boolean couldPluginBeEnabled() {
        return isOroPlatformDetected() && OroPlatformSettings.getInstance(project).couldPluginBeEnabled();
    }

    private boolean isOroPlatformDetected() {
        return VfsUtil.findRelativeFile(project.getBaseDir(), "vendor", "oro", "platform") != null;
    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ProjectConfigurator";
    }
}
