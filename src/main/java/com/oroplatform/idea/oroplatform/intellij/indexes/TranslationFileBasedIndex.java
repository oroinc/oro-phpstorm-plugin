package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.SimpleSuffixMatcher;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlScalarVisitor;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Map;

public class TranslationFileBasedIndex extends ScalarIndexExtension<String> {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.translations");

    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    private final SimpleSuffixMatcher suffixMatcher = new SimpleSuffixMatcher("Resources/translations/*.en.yml");

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(YAMLFileType.YML) {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return suffixMatcher.matches(file.getPath());
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return inputData -> {
            final Map<String, Void> index = new THashMap<>();

            if(!OroPlatformSettings.getInstance(inputData.getProject()).isPluginEnabled() || !(inputData.getPsiFile() instanceof YAMLFile)) {
                return index;
            }

            final YAMLFile file = (YAMLFile) inputData.getPsiFile();

            file.accept(new YamlScalarVisitor((trans, element) -> index.put(trans, null)));

            return index;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @Override
    public int getVersion() {
        return 1;
    }

}
