package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.psi.xml.XmlFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.intellij.indexes.services.ParameterXmlIndexer;
import com.oroplatform.idea.oroplatform.intellij.indexes.services.ParameterYmlIndexer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.HashMap;
import java.util.Map;

public class ServiceParametersFileBasedIndex extends FileBasedIndexExtension<String, String> {
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    public static final ID<String, String> KEY = ID.create("com.oroplatform.idea.oroplatform.service_parameters");
    private final DataIndexer<String, String, XmlFile> xmlIndexer = new ParameterXmlIndexer();
    private final DataIndexer<String, String, YAMLFile> ymlIndexer = new ParameterYmlIndexer();

    @NotNull
    @Override
    public ID<String, String> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, String, FileContent> getIndexer() {
        return inputData -> {
            final Map<String, String> index = new HashMap<>();

            if(inputData.getPsiFile() instanceof XmlFile) {
                final XmlFile file = (XmlFile) inputData.getPsiFile();
                index.putAll(xmlIndexer.map(file));
            } else if(inputData.getPsiFile() instanceof YAMLFile) {
                final YAMLFile file = (YAMLFile) inputData.getPsiFile();
                index.putAll(ymlIndexer.map(file));
            }

            return index;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<String> getValueExternalizer() {
        return keyDescriptor;
    }

    @Override
    public int getVersion() {
        return 0;
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
}
