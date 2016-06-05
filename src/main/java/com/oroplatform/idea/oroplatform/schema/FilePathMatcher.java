package com.oroplatform.idea.oroplatform.schema;

import com.intellij.psi.PsiFile;

class FilePathMatcher implements FileMatcher {
    private final String filePath;

    FilePathMatcher(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean matches(PsiFile file) {
        return file.getOriginalFile().getVirtualFile().getPath().endsWith(filePath);
    }
}
