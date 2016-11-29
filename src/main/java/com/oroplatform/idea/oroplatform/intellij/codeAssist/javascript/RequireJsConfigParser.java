package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequireJsConfigParser {
    public RequireJsConfig parse(YAMLFile configFile) {
        return new RequireJsConfig(parsePaths(configFile), parseMap(configFile));
    }

    private static Map<String, Map<String, String>> parseMap(YAMLFile configFile) {
        final YAMLKeyValue pathsKeyValue = YAMLUtil.getQualifiedKeyInFile(configFile, "config", "map");
        if(pathsKeyValue != null && pathsKeyValue.getValue() instanceof YAMLMapping) {
            final YAMLMapping pathsMapping = (YAMLMapping) pathsKeyValue.getValue();
            final Map<String, Map<String, String>> map = new HashMap<>();

            for (YAMLKeyValue yamlKeyValue : pathsMapping.getKeyValues()) {
                if(yamlKeyValue.getValue() instanceof YAMLMapping) {
                    map.put(yamlKeyValue.getKeyText(), extractMap((YAMLMapping)yamlKeyValue.getValue()));
                }
            }

            return map;
        }

        return Collections.emptyMap();
    }

    private static Map<String, String> parsePaths(YAMLFile configFile) {
        final YAMLKeyValue pathsKeyValue = YAMLUtil.getQualifiedKeyInFile(configFile, "config", "paths");
        if(pathsKeyValue != null && pathsKeyValue.getValue() instanceof YAMLMapping) {
            final YAMLMapping pathsMapping = (YAMLMapping) pathsKeyValue.getValue();
            return extractMap(pathsMapping);
        }
        return Collections.emptyMap();
    }

    private static Map<String, String> extractMap(YAMLMapping pathsMapping) {
        return pathsMapping.getKeyValues().stream()
            .collect(Collectors.toMap(YAMLKeyValue::getKeyText, YAMLKeyValue::getValueText, (v1, v2) -> v2));
    }
}
