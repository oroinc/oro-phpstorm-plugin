package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.psi.xml.XmlFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.intellij.indexes.services.ServiceXmlIndexer;
import com.oroplatform.idea.oroplatform.intellij.indexes.services.ServiceYamlIndexer;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Service;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Map;
import java.util.Set;

public class ServicesFileBasedIndex extends FileBasedIndexExtension<String, Service> {
    public static final ID<String, Service> KEY = ID.create("com.oroplatform.idea.oroplatform.services");
    private static final DataExternalizer<Service> serviceExternalizer = new JsonExternalizer<>(Service.class);
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    private final DataIndexer<Service, Void, XmlFile> xmlIndexer = new ServiceXmlIndexer();
    private final DataIndexer<Service, Void, YAMLFile> yamlIndexer = new ServiceYamlIndexer();

    @NotNull
    @Override
    public DataIndexer<String, Service, FileContent> getIndexer() {
        return inputData -> {
            final Map<String, Service> index = new THashMap<>();

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
        };
    }

    private void index(Set<Service> services, Map<String, Service> index) {
        services.stream()
            .filter(service -> service.getId() != null)
            .forEach(service -> index.put(service.getId(), service));
    }

    @NotNull
    @Override
    public ID<String, Service> getName() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return 3;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return ServiceFileFilter.INSTANCE;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @NotNull
    @Override
    public DataExternalizer<Service> getValueExternalizer() {
        return serviceExternalizer;
    }
}
