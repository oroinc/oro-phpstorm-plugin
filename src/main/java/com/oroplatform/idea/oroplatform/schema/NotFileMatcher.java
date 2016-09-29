package com.oroplatform.idea.oroplatform.schema;

import com.intellij.psi.PsiFile;

class NotFileMatcher implements FileMatcher {

    private final FileMatcher matcher;

    NotFileMatcher(FileMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(PsiFile file) {
        return !matcher.matches(file);
    }
}
