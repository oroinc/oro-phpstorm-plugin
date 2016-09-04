package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TranslationDomainFileBasedIndex extends ScalarIndexExtension<String> {
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.translation_domains");

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return file.getPath().contains("/cache/dev/translations/catalogue.en");
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
        return new TranslationsDataIndexer() {
            @Override
            void indexCatalogue(Map<String, Void> index, ArrayCreationExpression catalogue) {
                for (ArrayHashElement domainHash : catalogue.getHashElements()) {
                    if(domainHash.getKey() == null) continue;

                    index.put(StringUtil.stripQuotesAroundValue(domainHash.getKey().getText()), null);
                }
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}
