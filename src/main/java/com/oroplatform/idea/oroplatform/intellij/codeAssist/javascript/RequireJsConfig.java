package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequireJsConfig {
    private final Map<String, String> pathAliases = new HashMap<>();
    private Map<String, String> reversedPathAliases;
    private final Map<String, Map<String, String>> mappings = new HashMap<>();

    public RequireJsConfig(Map<String, String> pathAliases, Map<String, Map<String, String>> mappings) {
        this.pathAliases.putAll(pathAliases);
        this.mappings.putAll(mappings);
    }

    RequireJsConfig() {
        this(new HashMap<>(), new HashMap<>());
    }

    public Optional<String> getPathForAlias(String alias) {
        return Optional.ofNullable(pathAliases.get(alias));
    }

    public Optional<String> getAliasForPath(String path) {
        synchronized (this) {
            if(reversedPathAliases == null) {
                reversedPathAliases = new HashMap<>();
                pathAliases.forEach((key, value) -> reversedPathAliases.put(value, key));
            }
        }

        return Optional.ofNullable(reversedPathAliases.get(path));
    }

    public RequireJsConfig merge(RequireJsConfig config) {
        final Map<String, String> mergedPaths = new HashMap<>(pathAliases);
        mergedPaths.putAll(config.pathAliases);

        final Map<String, Map<String, String>> mergedMappings = new HashMap<>(mappings);
        config.mappings.forEach((key, value) -> mergedMappings.merge(key, value, RequireJsConfig::merge));

        return new RequireJsConfig(mergedPaths, mergedMappings);
    }

    public Optional<String> getPackageAliasFor(String pkg, String pkgForAlias) {
        return Optional.ofNullable(mappings.getOrDefault(pkg, Collections.emptyMap()).get(pkgForAlias))
            .map(Optional::of)
            .orElseGet(() -> Optional.ofNullable(mappings.getOrDefault("*", Collections.emptyMap()).get(pkgForAlias)));
    }

    private static <K,V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2) {
        final Map<K, V> result = new HashMap<>(map1);
        result.putAll(map2);

        return result;
    }
}
