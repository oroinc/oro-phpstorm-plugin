package com.oroplatform.idea.oroplatform.intellij.indexes.services;

import com.intellij.util.indexing.DataIndexer;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.symfony.Service;
import com.oroplatform.idea.oroplatform.symfony.Tag;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.oroplatform.idea.oroplatform.Functions.toStream;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.elementFilter;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getSequenceItems;

public class YamlIndexer implements DataIndexer<Service, Void, YAMLFile> {

    @NotNull
    @Override
    public Map<Service, Void> map(@NotNull YAMLFile file) {
        final YAMLKeyValue services = YAMLUtil.getQualifiedKeyInFile(file, "services");
        final Map<Service, Void> index = new THashMap<>();

        toStream(Optional.ofNullable(services))
            .flatMap(keyValue -> YamlPsiElements.getMappingsFrom(keyValue).stream())
            .flatMap(mapping -> mapping.getKeyValues().stream())
            .map(this::serviceElementToService)
            .forEach(service -> index.put(service, null));

        return index;
    }

    @NotNull
    private Service serviceElementToService(YAMLKeyValue serviceElement) {
        final List<YAMLMapping> serviceElements = YamlPsiElements.getMappingsFrom(serviceElement);
        final List<Tag> tags = serviceElements.stream()
            .flatMap(serviceMapping -> toStream(() -> serviceMapping.getKeyValueByKey("tags")))
            .flatMap(tagElements -> getSequenceItems(Arrays.asList(tagElements.getChildren())).stream())
            .flatMap(elementFilter(YAMLMapping.class))
            .map(tag -> new Tag(getValue(tag, "name"), getValue(tag, "alias")))
            .collect(Collectors.toList());

        final String className = serviceElements.stream()
            .flatMap(element -> toStream(element.getKeyValueByKey("class")))
            .flatMap(keyValue -> toStream(keyValue.getValueText()))
            .findFirst().orElse(null);

        return new Service(serviceElement.getKeyText(), tags, className);
    }

    private static String getValue(YAMLMapping mapping, String key) {
        final YAMLKeyValue keyValue = mapping.getKeyValueByKey(key);

        if(keyValue != null) {
            return keyValue.getValueText();
        }

        return null;
    }
}
