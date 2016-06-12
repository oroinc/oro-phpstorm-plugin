package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.intellij.indexes.services.XmlIndexer;
import com.oroplatform.idea.oroplatform.intellij.indexes.services.YamlIndexer;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Service;
import com.oroplatform.idea.oroplatform.symfony.Tag;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Map;
import java.util.Set;

abstract class ServicesFileBasedIndex extends ScalarIndexExtension<String> {
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    private final DataIndexer<Service, Void, XmlFile> xmlIndexer = new XmlIndexer();
    private final DataIndexer<Service, Void, YAMLFile> yamlIndexer = new YamlIndexer();
    private final String indexTag;

    ServicesFileBasedIndex(String indexTag) {
        this.indexTag = indexTag;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return new DataIndexer<String, Void, FileContent>() {
            @NotNull
            @Override
            public Map<String, Void> map(@NotNull FileContent inputData) {
                final Map<String, Void> index = new THashMap<String, Void>();

                if(!OroPlatformSettings.getInstance(inputData.getProject()).isPluginEnabled()) {
                    return index;
                }

                if(inputData.getPsiFile() instanceof XmlFile) {
                    final Set<Service> services = xmlIndexer.map((XmlFile) inputData.getPsiFile()).keySet();
                    index(services, index);
                } else if(inputData.getPsiFile() instanceof YAMLFile) {
                    final Set<Service> services = yamlIndexer.map((YAMLFile) inputData.getPsiFile()).keySet();
                    index(services, index);
                }

                return index;
            }
        };
    }

    private void index(Set<Service> services, Map<String, Void> index) {
        for (Service service : services) {
            for (Tag tag : service.getTags()) {
                if(indexTag.equals(tag.getName()) && tag.getAlias() != null) {
                    for (String alias : tag.getAlias().split("\\|")) {
                        index.put(alias, null);
                    }
                }
            }
        }
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(XmlFileType.INSTANCE, YAMLFileType.YML) {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return (file.getFileType().equals(XmlFileType.INSTANCE) || file.getFileType().equals(YAMLFileType.YML)) && file.getPath().contains("/Resources/config/");
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 1;
    }

}
