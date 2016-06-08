package com.oroplatform.idea.oroplatform.intellij.indexes.services;

import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.DataIndexer;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesFileBasedIndex;
import com.oroplatform.idea.oroplatform.symfony.Service;
import com.oroplatform.idea.oroplatform.symfony.Tag;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.*;

public class YamlIndexer implements DataIndexer<String, Collection<Service>, YAMLFile> {
    private final ServicesFileBasedIndex.ServiceIndexPutter indexPutter;

    public YamlIndexer(ServicesFileBasedIndex.ServiceIndexPutter indexPutter) {
        this.indexPutter = indexPutter;
    }

    @NotNull
    @Override
    public Map<String, Collection<Service>> map(@NotNull YAMLFile file) {
        Map<String, Collection<Service>> index = new THashMap<String, Collection<Service>>();

        final YAMLKeyValue services = YAMLUtil.getQualifiedKeyInFile(file, "services");

        if(services != null && services.getValue() != null && services.getValue() instanceof YAMLMapping) {
            for (YAMLKeyValue serviceElement : ((YAMLMapping) services.getValue()).getKeyValues()) {
                if(serviceElement.getValue() != null && serviceElement.getValue() instanceof YAMLMapping) {
                    YAMLMapping serviceMapping = (YAMLMapping) serviceElement.getValue();
                    final YAMLKeyValue tagElements = serviceMapping.getKeyValueByKey("tags");

                    Collection<Tag> tags = new THashSet<Tag>();

                    if(tagElements != null) {
                        for (PsiElement tag : getSequenceItems(Arrays.asList(tagElements.getChildren()))) {
                            if(tag instanceof YAMLMapping) {
                                String name = getValue((YAMLMapping) tag, "name");
                                String alias = getValue((YAMLMapping) tag, "alias");

                                tags.add(new Tag(name, alias));
                            }
                        }
                    }

                    final String className = getValue(serviceMapping, "class");
                    final Service service = new Service(serviceElement.getKeyText(), className, tags);
                    indexPutter.put(service, index);
                }
            }
        }


        return index;
    }

    private static String getValue(YAMLMapping mapping, String key) {
        final YAMLKeyValue keyValue = mapping.getKeyValueByKey(key);

        if(keyValue != null) {
            return keyValue.getValueText();
        }

        return null;
    }
}
