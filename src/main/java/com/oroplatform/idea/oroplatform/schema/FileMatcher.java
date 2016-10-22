package com.oroplatform.idea.oroplatform.schema;

import com.intellij.psi.PsiFile;

@FunctionalInterface
public interface FileMatcher {
    boolean matches(PsiFile file);
}
