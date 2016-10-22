package com.oroplatform.idea.oroplatform.intellij;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.oroplatform.idea.oroplatform.OroPlatformBundle;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

class OroNotifications {
    private final static NotificationGroup GROUP = NotificationGroup.balloonGroup(OroPlatformBundle.message("notifications.group"));

    static void showPluginEnableNotification(@NotNull final Project project) {
        final Notification notification = GROUP.createNotification(
            OroPlatformBundle.message("notifications.enablePluginTitle"),
            OroPlatformBundle.message("notifications.enablePlugin"),
            NotificationType.INFORMATION,
            (thisNotification, event) -> {
                thisNotification.expire();

                final OroPlatformSettings settings = OroPlatformSettings.getInstance(project);

                if("enable".equals(event.getDescription())) {
                    settings.setPluginEnabled(true);
                    showPluginEnabledNotification(project);
                } else if("dismiss".equals(event.getDescription())) {
                    settings.setPluginEnableDismissed(true);
                }
            });

        Notifications.Bus.notify(notification, project);
    }

    private static void showPluginEnabledNotification(@NotNull final Project project) {
        Notifications.Bus.notify(GROUP.createNotification(
            OroPlatformBundle.message("notifications.group"),
            OroPlatformBundle.message("notifications.pluginEnabled"),
            NotificationType.INFORMATION, null
        ), project);
    }
}
