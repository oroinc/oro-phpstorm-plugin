package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequireJsConfig {
    private final Map<String, String> pathAliases = new HashMap<>();
    private Map<String, String> reversedPathAliases;
    private final Map<String, Map<String, String>> mappings = new HashMap<>();
    private Map<String, Map<String, String>> reversedMappings;

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
                reversedPathAliases = reverseMap(pathAliases);
            }
        }

        return Optional.ofNullable(reversedPathAliases.get(path));
    }

    @NotNull
    private Map<String, String> reverseMap(Map<String, String> map) {
        final Map<String, String> reversedMap = new HashMap<>();
        map.forEach((key, value) -> reversedMap.put(value, key));
        return reversedMap;
    }

    public RequireJsConfig merge(RequireJsConfig config) {
        final Map<String, String> mergedPaths = new HashMap<>(pathAliases);
        mergedPaths.putAll(config.pathAliases);

        final Map<String, Map<String, String>> mergedMappings = new HashMap<>(mappings);
        config.mappings.forEach((key, value) -> mergedMappings.merge(key, value, RequireJsConfig::merge));

        return new RequireJsConfig(mergedPaths, mergedMappings);
    }

    public Optional<String> getPackageAliasFor(String pkg, String pkgForAlias) {
        return getValueFromMapOfMaps(mappings, pkg, pkgForAlias);
    }

    private Optional<String> getValueFromMapOfMaps(Map<String, Map<String, String>> mapOfMaps, String key1, String key2) {
        return Optional.ofNullable(mapOfMaps.getOrDefault(key1, Collections.emptyMap()).get(key2))
            .map(Optional::of)
            .orElseGet(() -> Optional.ofNullable(mapOfMaps.getOrDefault("*", Collections.emptyMap()).get(key2)));
    }

    public Optional<String> getPackageForAlias(String pkg, String pkgAlias) {
        synchronized (this) {
            if(reversedMappings == null) {
                reversedMappings = new HashMap<>();
                mappings.forEach((key, value) -> reversedMappings.put(key, reverseMap(value)));
            }
        }

        return getValueFromMapOfMaps(reversedMappings, pkg, pkgAlias);
    }

    private static <K,V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2) {
        final Map<K, V> result = new HashMap<>(map1);
        result.putAll(map2);

        return result;
    }
}
