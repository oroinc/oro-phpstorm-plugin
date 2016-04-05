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

        final String vendor = entityIndex == 1 ? "" : parts.get(0);
        final String bundle = parts.get(entityIndex - 1);

        return vendor+bundle+":"+StringUtil.join(parts.subList(entityIndex + 1, parts.size()), "\\");
    }

    @NotNull
    public static String getSimpleName(@NotNull String className) {
        final String[] parts = className.split("(\\\\)|:");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }
}
