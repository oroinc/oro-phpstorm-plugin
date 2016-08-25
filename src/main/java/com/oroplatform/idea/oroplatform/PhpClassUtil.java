package com.oroplatform.idea.oroplatform;

import org.jetbrains.annotations.NotNull;

public class PhpClassUtil {
    @NotNull
    public static String getSimpleName(@NotNull String className) {
        final String[] parts = className.split("(\\\\)|:");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }

    public static boolean isTestOrGeneratedClass(@NotNull String className) {
        return className.contains("\\__CG__\\") || className.contains("\\Tests\\") || className.startsWith("__");
    }
}
