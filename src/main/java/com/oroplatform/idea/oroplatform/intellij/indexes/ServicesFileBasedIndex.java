package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.intellij.indexes.services.XmlIndexer;
import com.oroplatform.idea.oroplatform.intellij.indexes.services.YamlIndexer;
import com.oroplatform.idea.oroplatform.symfony.Service;
import com.oroplatform.idea.oroplatform.symfony.Tag;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Collection;
import java.util.Map;

public class ServicesFileBasedIndex extends FileBasedIndexExtension<String, Collection<Service>> {
    static final ID<String, Collection<Service>> KEY = ID.create("com.oroplatform.idea.oroplatform.services");
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    private final DataExternalizer<Collection<Service>> valueExternalizer = new CollectionExternalizer<Service>(new JsonExternalizer<Service>(Service.class));
    private final ServiceIndexPutter indexPutter = new TagServiceIndexPutter();
    private final DataIndexer<String, Collection<Service>, XmlFile> xmlIndexer = new XmlIndexer(indexPutter);
    private final DataIndexer<String, Collection<Service>, YAMLFile> yamlIndexer = new YamlIndexer(indexPutter);

    @NotNull
    @Override
    public ID<String, Collection<Service>> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Collection<Service>, FileContent> getIndexer() {
        return new DataIndexer<String, Collection<Service>, FileContent>() {
            @NotNull
            @Override
            public Map<String, Collection<Service>> map(@NotNull FileContent inputData) {
                final Map<String, Collection<Service>> index = new THashMap<String, Collection<Service>>();

                if(inputData.getPsiFile() instanceof XmlFile) {
                    index.putAll(xmlIndexer.map((XmlFile) inputData.getPsiFile()));
                } else if(inputData.getPsiFile() instanceof YAMLFile) {
                    index.putAll(yamlIndexer.map((YAMLFile) inputData.getPsiFile()));
                }

                return index;
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Collection<Service>> getValueExternalizer() {
        return valueExternalizer;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new FileBasedIndex.InputFilter() {
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

    public interface ServiceIndexPutter {
        void put(Service service, Map<String, Collection<Service>> index);
    }

    private static class TagServiceIndexPutter implements ServiceIndexPutter {
        @Override
        public void put(Service service, Map<String, Collection<Service>> index) {
            Collection<Tag> serviceTags = service.getTags();
            for (Tag tag : serviceTags) {
                if(tag.getName() != null) {
                    if(index.containsKey(tag.getName())) {
                        index.get(tag.getName()).add(service);
                    } else {
                        Collection<Service> services = new THashSet<Service>();
                        services.add(service);
                        index.put(tag.getName(), services);
                    }
                }
            }
        }
    }
}
