package com.oroplatform.idea.oroplatform.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(
    name = "OroPlatformPluginSettings",
    storages = {
        @Storage("$PROJECT_CONFIG_DIR$/oroPlatform.xml")
    }
)
public class OroPlatformSettings implements PersistentStateComponent<Element>, ModificationTracker {
    static final String DEFAULT_APP_DIRECTORY = "bin";

    private final Project project;

    public OroPlatformSettings(Project project) {
        this.project = project;
    }

    @NotNull
    public static OroPlatformSettings getInstance(@NotNull Project project) {
        return project.getService(OroPlatformSettings.class);
    }

    private String appDir = DEFAULT_APP_DIRECTORY;
    private VerboseBoolean pluginEnabled = new VerboseBoolean(false, false);
    private long lastModifiedTimeStamp = 0;

    public String getAppDir() {
        return appDir;
    }

    public VirtualFile getAppVirtualDir() {
        return getProjectRoot(project).findFileByRelativePath(
                ApplicationManager.getApplication().isUnitTestMode()
                        ? "app"
                        : OroPlatformSettings.getInstance(project).getAppDir()
        );
    }

    private static VirtualFile getProjectRoot(Project project) {
        return ApplicationManager.getApplication().isUnitTestMode()
                ? Objects.requireNonNull(VirtualFileManager.getInstance().findFileByUrl("temp:///")).findChild("src")
                : ProjectUtil.guessProjectDir(project);
    }

    void setAppDir(String appDir) {
        this.appDir = appDir;
        this.lastModifiedTimeStamp = System.currentTimeMillis();
    }

    public void setPluginEnabled(boolean f) {
        pluginEnabled = pluginEnabled.set(f);
        lastModifiedTimeStamp = System.currentTimeMillis();
    }

    public boolean isPluginEnabled() {
        return pluginEnabled.isTrue();
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

        Element timeStampElement = new Element("lastModifiedTimeStamp");
        timeStampElement.setText(String.valueOf(lastModifiedTimeStamp));

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
        Element timeStampElement = state.getChild("lastModifiedTimeStamp");

        if(appDirElement != null) {
            appDir = appDirElement.getText();
        }

        if(pluginEnabledElement != null) {
            pluginEnabled = readVerboseBoolean(pluginEnabledElement);
        }

        if(timeStampElement != null) {
            try {
                lastModifiedTimeStamp = Long.getLong(timeStampElement.getText());
            } catch (NumberFormatException e) {
                //ignore
            }
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

    @Override
    public long getModificationCount() {
        return lastModifiedTimeStamp;
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
