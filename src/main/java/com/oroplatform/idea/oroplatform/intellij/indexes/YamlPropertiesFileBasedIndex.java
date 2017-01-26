package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.SimpleSuffixMatcher;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.*;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getMappingsFrom;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getPropertyFrom;

class YamlPropertiesFileBasedIndex extends ScalarIndexExtension<String> {

    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    private final ID<String, Void> key;
    private final Collection<IndexBlueprint> indexBlueprints = new LinkedList<>();

    YamlPropertiesFileBasedIndex(ID<String, Void> key, String filepathSuffix, PropertyPath propertyPath) {
        this(key, new IndexBlueprint(filepathSuffix, propertyPath));
    }

    YamlPropertiesFileBasedIndex(ID<String, Void> key, IndexBlueprint... indexBlueprints) {
        this.key = key;
        this.indexBlueprints.addAll(Arrays.asList(indexBlueprints));
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return key;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return inputData -> {
            final Map<String, Void> index = new THashMap<>();

            if(!OroPlatformSettings.getInstance(inputData.getProject()).isPluginEnabled()) {
                return index;
            }

            final YAMLFile file = (YAMLFile) inputData.getPsiFile();

            for (IndexBlueprint indexBlueprint : indexBlueprints) {
                if(indexBlueprint.filepathSuffixMatcher.matches(inputData.getFile().getPath())) {
                    final Collection<String> values =
                        getPropertyFrom(indexBlueprint.propertyPath, getMappingsFrom(file), Collections.emptySet());

                    for (String value : values) {
                        index.put(value, null);
                    }
                }

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
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> {
            if(!file.getFileType().equals(YAMLFileType.YML)) return false;

            for (IndexBlueprint indexBlueprint : indexBlueprints) {
                if(indexBlueprint.filepathSuffixMatcher.matches(file.getPath())) return true;
            }

            return false;
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

    static class IndexBlueprint {
        private final SimpleSuffixMatcher filepathSuffixMatcher;
        private final PropertyPath propertyPath;

        IndexBlueprint(String filepathSuffix, PropertyPath propertyPath) {
            this.filepathSuffixMatcher = new SimpleSuffixMatcher(filepathSuffix);
            this.propertyPath = propertyPath;
        }
    }
}
