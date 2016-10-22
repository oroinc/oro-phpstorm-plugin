package com.oroplatform.idea.oroplatform.schema;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import gnu.trove.THashSet;

import java.util.Arrays;
import java.util.Set;

class ExtensionFileFilter implements VirtualFileFilter {
    private final Set<String> extensions = new THashSet<>();

    ExtensionFileFilter(String... extensions) {
        this.extensions.addAll(Arrays.asList(extensions));
    }

    @Override
    public boolean accept(VirtualFile file) {
        return extensions.contains(file.getExtension());
    }
}
