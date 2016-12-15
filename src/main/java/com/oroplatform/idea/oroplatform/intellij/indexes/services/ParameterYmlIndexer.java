package com.oroplatform.idea.oroplatform.intellij.indexes.services;

import com.intellij.util.indexing.DataIndexer;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Map;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

public class ParameterYmlIndexer implements DataIndexer<String, String, YAMLFile> {
    @NotNull
    @Override
    public Map<String, String> map(@NotNull YAMLFile file) {
        final Map<String, String> index = new THashMap<>();
        final YAMLKeyValue parameters = YAMLUtil.getQualifiedKeyInFile(file, "parameters");

        toStream(parameters)
            .flatMap(keyValue -> YamlPsiElements.getMappingsFrom(keyValue).stream())
            .flatMap(mapping -> mapping.getKeyValues().stream())
            .forEach(keyValue -> index.put(keyValue.getKeyText(), keyValue.getValueText()));


        return index;
    }
}
