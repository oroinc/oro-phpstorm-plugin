package com.oroplatform.idea.oroplatform.intellij;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import gnu.trove.THashSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExtensionFileFilter implements VirtualFileFilter {
    private final Set<String> extensions = new HashSet<>();

    public ExtensionFileFilter(String... extensions) {
        this.extensions.addAll(Arrays.asList(extensions));
    }

    @Override
    public boolean accept(VirtualFile file) {
        return extensions.contains(file.getExtension());
    }
}
