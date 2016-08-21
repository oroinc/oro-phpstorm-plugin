package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getMappingsFrom;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getPropertyValuesFrom;

class YamlPropertiesFileBasedIndex extends ScalarIndexExtension<String> {

    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    private final ID<String, Void> key;
    private final String filepathSuffix;
    private final PropertyPath propertyPath;

    YamlPropertiesFileBasedIndex(ID<String, Void> key, String filepathSuffix, PropertyPath propertyPath) {
        this.key = key;
        this.filepathSuffix = filepathSuffix;
        this.propertyPath = propertyPath;
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return key;
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

                final Collection<String> values =
                    getPropertyValuesFrom(propertyPath, getMappingsFrom(file), Collections.<PsiElement>emptySet());

                for (String value : values) {
                    index.put(value, null);
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
                return file.getFileType().equals(YAMLFileType.YML) && file.getPath().endsWith(filepathSuffix);
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
