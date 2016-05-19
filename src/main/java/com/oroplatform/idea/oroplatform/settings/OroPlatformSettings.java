package com.oroplatform.idea.oroplatform.settings;

import com.intellij.openapi.application.ApplicationManager;
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
    private VerboseBoolean pluginEnabled = new VerboseBoolean(false, false);

    public String getAppDir() {
        return appDir;
    }

    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    public void setPluginEnabled(boolean f) {
        pluginEnabled = pluginEnabled.set(f);
    }

    public boolean isPluginEnabled() {
        return ApplicationManager.getApplication().isUnitTestMode() || pluginEnabled.isTrue();
    }

    public boolean couldPluginBeEnabled() {
        return !pluginEnabled.isTrue() && !pluginEnabled.isDismissed();
    }

    public void setPluginEnableDismissed(boolean f) {
        pluginEnabled = pluginEnabled.setDismissed(f);
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("OroPlatformSettings");

        Element appDirElement = new Element("appDir");
        appDirElement.setText(appDir);

        Element pluginEnabledElement = new Element("pluginEnabled");
        writeVerboseBoolean(pluginEnabledElement, pluginEnabled);

        element.addContent(appDirElement);
        element.addContent(pluginEnabledElement);

        return element;
    }

    private void writeVerboseBoolean(Element parentElement, VerboseBoolean verboseBoolean) {
        Element valueElement = new Element("value");
        valueElement.setText(Boolean.toString(verboseBoolean.isTrue()));
        Element dismissedValue = new Element("dismissed");
        dismissedValue.setText(Boolean.toString(verboseBoolean.isDismissed()));

        parentElement.addContent(valueElement);
        parentElement.addContent(dismissedValue);
    }

    @Override
    public void loadState(Element state) {
        Element appDirElement = state.getChild("appDir");
        Element pluginEnabledElement = state.getChild("pluginEnabled");

        if(appDirElement != null) {
            appDir = appDirElement.getText();
        }

        if(pluginEnabledElement != null) {
            pluginEnabled = readVerboseBoolean(pluginEnabledElement);
        }
    }

    private VerboseBoolean readVerboseBoolean(Element verboseBooleanElement) {
        final Element value = verboseBooleanElement.getChild("value");
        final Element dismissed = verboseBooleanElement.getChild("dismissed");

        if(value != null && dismissed != null) {
            return new VerboseBoolean("true".equals(value.getText()), "true".equals(dismissed.getText()));
        }

        return new VerboseBoolean(false, false);
    }

    private static class VerboseBoolean {
        private final boolean value;
        private final boolean dismissed;

        private VerboseBoolean(boolean value, boolean dismissed) {
            this.value = value;
            this.dismissed = dismissed;
        }

        boolean isTrue() {
            return value;
        }

        boolean isDismissed() {
            return dismissed;
        }

        VerboseBoolean set(boolean f) {
            return new VerboseBoolean(f, true);
        }

        VerboseBoolean setDismissed(boolean f) {
            return new VerboseBoolean(value, f);
        }
    }
}
