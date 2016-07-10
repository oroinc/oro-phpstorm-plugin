package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.Map;

public class DatagridFileBasedIndex extends ScalarIndexExtension<String> {

    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.datagrids");

    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
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

                final YAMLFile file = (YAMLFile) inputData.getPsiFile();
                final YAMLKeyValue datagrids = YAMLUtil.getQualifiedKeyInFile(file, "datagrid");

                if(datagrids != null && datagrids.getValue() != null && datagrids.getValue() instanceof YAMLMapping) {
                    for (YAMLKeyValue datagridValue : ((YAMLMapping) datagrids.getValue()).getKeyValues()) {
                        index.put(datagridValue.getKeyText(), null);
                    }
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
    public FileBasedIndex.InputFilter getInputFilter() {
        return new FileBasedIndex.InputFilter() {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return file.getFileType().equals(YAMLFileType.YML) && file.getPath().endsWith(Schemas.FilePathPatterns.DATAGRID);
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}
