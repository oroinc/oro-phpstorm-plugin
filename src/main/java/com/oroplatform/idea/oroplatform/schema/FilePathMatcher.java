package com.oroplatform.idea.oroplatform.schema;

import com.intellij.psi.PsiFile;
import com.oroplatform.idea.oroplatform.SimpleSuffixMatcher;

class FilePathMatcher implements FileMatcher {
    private final SimpleSuffixMatcher matcher;

    FilePathMatcher(String filePath) {
        this.matcher = new SimpleSuffixMatcher(filePath);
    }

    @Override
    public boolean matches(PsiFile file) {
        return matcher.matches(file.getOriginalFile().getVirtualFile().getPath());
    }
}
