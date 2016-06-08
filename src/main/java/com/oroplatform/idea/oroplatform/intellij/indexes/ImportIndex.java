package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.HashSet;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ImportIndex extends FileBasedIndexExtension<String, Collection<String>> {

    public static final ID<String, Collection<String>> KEY = ID.create("com.oroplatform.idea.oroplatform.import");
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, Collection<String>> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Collection<String>, FileContent> getIndexer() {
        return new DataIndexer<String, Collection<String>, FileContent>() {
            @NotNull
            @Override
            public Map<String, Collection<String>> map(@NotNull FileContent inputData) {
                Map<String, Collection<String>> index = new THashMap<String, Collection<String>>();

                if(!OroPlatformSettings.getInstance(inputData.getProject()).isPluginEnabled()) {
                    return index;
                }

                final Set<String> importedFilePaths = getImportedFilePaths(inputData.getFile().getParent(), (YAMLFile) inputData.getPsiFile());
                if(!importedFilePaths.isEmpty()) {
                    index.put(inputData.getFile().getPath(), importedFilePaths);
                }

                return index;
            }

            private Set<String> getImportedFilePaths(VirtualFile parent, YAMLFile file) {
                final Set<String> paths = new HashSet<String>();

                for (YAMLMapping mapping : YamlPsiElements.getMappingsFrom(file)) {
                    final YAMLKeyValue imports = mapping.getKeyValueByKey("imports");
                    if(imports != null && imports.getValue() != null) {
                        for (PsiElement sequenceItem : YamlPsiElements.getSequenceItems(Collections.singletonList(imports.getValue()))) {
                            if(sequenceItem instanceof YAMLMapping) {
                                final YAMLKeyValue resource = ((YAMLMapping) sequenceItem).getKeyValueByKey("resource");
                                if(resource != null) {
                                    paths.add(parent.getPath() + "/" + resource.getValueText());
                                }
                            }
                        }
                    }
                }

                return paths;
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
    public DataExternalizer<Collection<String>> getValueExternalizer() {
        return new CollectionExternalizer<String>(keyDescriptor);
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new FileBasedIndex.InputFilter() {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return file.getFileType().equals(YAMLFileType.YML) && file.getPath().contains("Resources/config");
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
