package com.oroplatform.idea.oroplatform;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpClassUtil {
    @Nullable
    public static String getDoctrineShortcutClassName(@NotNull String fqn) {
        final String fixedFQN = StringUtil.trimStart(fqn, "\\");
        final String[] parts = fixedFQN.split("\\\\");

        if(parts.length < 3 || !parts[parts.length - 2].equals("Entity")) return null;

        final String vendor = parts.length == 3 ? "" : parts[0];
        final String bundle = parts[parts.length - 3];

        return vendor+bundle+":"+parts[parts.length - 1];
    }

    @NotNull
    public static String getSimpleName(@NotNull String className) {
        final String[] parts = className.split("(\\\\)|:");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }
}
