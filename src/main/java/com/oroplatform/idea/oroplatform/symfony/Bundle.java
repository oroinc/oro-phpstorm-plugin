package com.oroplatform.idea.oroplatform.symfony;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class Bundle {
    private final String name;
    private final String namespaceName;

    public Bundle(@NotNull String namespaceName) {
        this(Arrays.asList(StringUtil.trimStart(namespaceName, "\\").split("\\\\")));
    }

    public Bundle(@NotNull List<String> namespaceNameParts) {
        this.name = getBundleName(namespaceNameParts);
        this.namespaceName = "\\"+StringUtil.join(namespaceNameParts, "\\");
    }

    public String getName() {
        return name;
    }

    public String getNamespaceName() {
        return namespaceName;
    }

    @Nullable
    private static String getBundleName(@NotNull List<String> bundlePathParts) {
        if(bundlePathParts.size() < 1) return null;
        if(bundlePathParts.size() == 1) return bundlePathParts.get(0);

        return bundlePathParts.get(0) + bundlePathParts.get(bundlePathParts.size() - 1);
    }
}
