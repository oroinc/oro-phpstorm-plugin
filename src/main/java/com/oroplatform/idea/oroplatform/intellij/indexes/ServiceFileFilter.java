package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;

class ServiceFileFilter extends DefaultFileTypeSpecificInputFilter {

    static final FileBasedIndex.InputFilter INSTANCE = new ServiceFileFilter();

    private ServiceFileFilter() {
        super(XmlFileType.INSTANCE, YAMLFileType.YML);
    }

    @Override
    public boolean acceptInput(@NotNull VirtualFile file) {
        return (file.getFileType().equals(XmlFileType.INSTANCE) || file.getFileType().equals(YAMLFileType.YML)) && file.getPath().contains("/Resources/config/");
    }
}
