package com.oroplatform.idea.oroplatform;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PhpClassUtil {
    @Nullable
    public static String getDoctrineShortcutClassName(@NotNull String fqn) {
        final String fixedFQN = StringUtil.trimStart(fqn, "\\");
        final List<String> parts = Arrays.asList(fixedFQN.split("\\\\"));
        final int entityIndex = parts.indexOf("Entity");

        if(parts.size() < 3 || entityIndex <= 0 || entityIndex == parts.size() - 1) return null;

        return getBundleName(parts.subList(0, entityIndex))+":"+StringUtil.join(parts.subList(entityIndex + 1, parts.size()), "\\");
    }

    //TODO: extract Bundle class
    public static String getBundleName(@NotNull String namespaceName) {
        return getBundleName(Arrays.asList(StringUtil.trimStart(namespaceName, "\\").split("\\\\")));
    }

    @Nullable
    private static String getBundleName(@NotNull List<String> bundlePathParts) {
        if(bundlePathParts.size() < 1) return null;
        if(bundlePathParts.size() == 1) return bundlePathParts.get(0);

        return bundlePathParts.get(0) + bundlePathParts.get(bundlePathParts.size() - 1);
    }

    @NotNull
    public static String getSimpleName(@NotNull String className) {
        final String[] parts = className.split("(\\\\)|:");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }

    public static boolean isTestOrGeneratedClass(@NotNull String className) {
        return className.contains("\\__CG__\\") || className.contains("\\Tests\\") || className.startsWith("__");
    }
}
