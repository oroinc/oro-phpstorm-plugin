package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import gnu.trove.THashMap;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.*;

import java.util.*;
import java.util.stream.Collectors;

public class RequireJsConfigParser {
    public RequireJsConfig parse(YAMLFile configFile) {
        return new RequireJsConfig(parsePaths(configFile), parseMap(configFile), parseDynamicImports(configFile));
    }

    private static Map<String, Map<String, String>> parseMap(YAMLFile configFile) {
        final YAMLKeyValue pathsKeyValue = YAMLUtil.getQualifiedKeyInFile(configFile, "map");
        if (pathsKeyValue != null && pathsKeyValue.getValue() instanceof YAMLMapping pathsMapping) {
            final Map<String, Map<String, String>> map = new THashMap<>();

            for (YAMLKeyValue yamlKeyValue : pathsMapping.getKeyValues()) {
                if (yamlKeyValue.getValue() instanceof YAMLMapping) {
                    map.put(yamlKeyValue.getKeyText(), extractReversedMap((YAMLMapping) yamlKeyValue.getValue()));
                }
            }

            return map;
        }

        return Collections.emptyMap();
    }

    private static Map<String, String> parsePaths(YAMLFile configFile) {
        final YAMLKeyValue pathsKeyValue = YAMLUtil.getQualifiedKeyInFile(configFile, "aliases");
        if (pathsKeyValue != null && pathsKeyValue.getValue() instanceof YAMLMapping pathsMapping) {
            return extractMap(pathsMapping);
        }
        return Collections.emptyMap();
    }

    private static List<String> parseDynamicImports(YAMLFile configFile) {
        final YAMLKeyValue dynamicImportsValue = YAMLUtil.getQualifiedKeyInFile(configFile, "dynamic-imports");

        if (dynamicImportsValue != null && dynamicImportsValue.getValue() instanceof YAMLMapping yamlMapping) {
            return extractDynamicImports(yamlMapping);
        }

        return new ArrayList<>();
    }

    private static List<String> extractDynamicImports(YAMLMapping dynamicImportMappings) {
        List<List<String>> values = Collections.singletonList(dynamicImportMappings.getKeyValues().stream()
                .map(YAMLKeyValue::getValue)
                .filter(yamlValue -> yamlValue instanceof YAMLSequence)
                .map(yamlSequence -> extractDynamicImportsFromSequence((YAMLSequence) yamlSequence))
                .flatMap(List::stream)
                .toList());


        return new ArrayList<>(values.get(0));
    }

    private static List<String> extractDynamicImportsFromSequence(YAMLSequence sequence) {
        return sequence.getItems().stream()
                .map(YAMLSequenceItem::getValue)
                .filter(Objects::nonNull)
                .map(YAMLValue::getText)
                .collect(Collectors.toList());
    }

    private static Map<String, String> extractMap(YAMLMapping pathsMapping) {
        return pathsMapping.getKeyValues().stream()
                .map(yamlKeyValue -> new AbstractMap.SimpleEntry<>(
                        yamlKeyValue.getValueText(),
                        yamlKeyValue.getKeyText().replace("$", "")
                ))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (v1, v2) -> v2));
    }

    private static Map<String, String> extractReversedMap(YAMLMapping pathsMapping) {
        return pathsMapping.getKeyValues().stream()
                .collect(Collectors.toMap(YAMLKeyValue::getValueText, YAMLKeyValue::getKeyText, (v1, v2) -> v2));
    }
}
