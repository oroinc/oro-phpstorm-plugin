package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequireJsConfig {
    private final Map<String, String> pathAliases = new HashMap<>();
    private final Map<String, Map<String, String>> mappings = new HashMap<>();

    public RequireJsConfig(Map<String, String> pathAliases, Map<String, Map<String, String>> mappings) {
        this.pathAliases.putAll(pathAliases);
        this.mappings.putAll(mappings);
    }

    public Optional<String> getPathAliasFor(String path) {
        return Optional.ofNullable(pathAliases.get(path));
    }

    public RequireJsConfig merge(RequireJsConfig config) {
        final Map<String, String> mergedPaths = new HashMap<>(pathAliases);
        mergedPaths.putAll(config.pathAliases);

        final Map<String, Map<String, String>> mergedMappings = new HashMap<>(mappings);
        config.mappings.forEach((key, value) -> mergedMappings.merge(key, value, RequireJsConfig::merge));

        return new RequireJsConfig(mergedPaths, mergedMappings);
    }

    public Map<String, String> getPackageAliasesFor(String pkg) {
        return new HashMap<>(mappings.getOrDefault(pkg, Collections.emptyMap()));
    }

    private static <K,V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2) {
        final Map<K, V> result = new HashMap<>(map1);
        result.putAll(map2);

        return result;
    }
}
