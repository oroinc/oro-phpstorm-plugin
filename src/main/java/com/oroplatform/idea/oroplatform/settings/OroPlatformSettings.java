package com.oroplatform.idea.oroplatform.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "OroPlatformPluginSettings",
    storages = {
        @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
        @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/oroPlatform.xml", scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class OroPlatformSettings implements PersistentStateComponent<Element> {
    public static final String DEFAULT_APP_DIRECTORY = "app";

    @NotNull
    public static OroPlatformSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, OroPlatformSettings.class);
    }

    private String appDir = DEFAULT_APP_DIRECTORY;

    public String getAppDir() {
        return appDir;
    }

    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("OroPlatformSettings");

        Element appDirElement = new Element("appDir");
        appDirElement.setText(appDir);
        element.addContent(appDirElement);

        return element;
    }

    @Override
    public void loadState(Element state) {
        Element appDirElement = state.getChild("appDir");

        if(appDirElement != null) {
            appDir = appDirElement.getText();
        }
    }
}
