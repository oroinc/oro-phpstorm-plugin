package com.oroplatform.idea.oroplatform.intellij;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.oroplatform.idea.oroplatform.OroPlatformBundle;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

class OroNotifications {
    private final static NotificationGroup GROUP = NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Oro Notifications");

    static void showPluginEnableNotification(@NotNull final Project project) {
        final Notification notification = GROUP.createNotification(
                OroPlatformBundle.message("notifications.enablePluginTitle"),
                OroPlatformBundle.message("notifications.enablePlugin"),
                NotificationType.INFORMATION);

        final OroPlatformSettings settings = OroPlatformSettings.getInstance(project);

        notification.addAction(new NotificationAction(OroPlatformBundle.message("notifications.enablePlugin.accept")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                notification.expire();
                settings.setPluginEnabled(true);
                showPluginEnabledNotification(project);
            }
        });

        notification.addAction(new NotificationAction(
                OroPlatformBundle.message("notifications.enablePlugin.dismiss")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                notification.expire();
                settings.setPluginEnableDismissed(true);
            }
        });
        Notifications.Bus.notify(notification, project);
    }

    private static void showPluginEnabledNotification(@NotNull final Project project) {
        Notifications.Bus.notify(GROUP.createNotification(
                OroPlatformBundle.message("notifications.group"),
                OroPlatformBundle.message("notifications.pluginEnabled"),
                NotificationType.INFORMATION
        ), project);
    }
}
