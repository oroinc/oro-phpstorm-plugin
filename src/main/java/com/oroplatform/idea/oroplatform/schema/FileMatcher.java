package com.oroplatform.idea.oroplatform.schema;

import com.intellij.psi.PsiFile;

public interface FileMatcher {
    boolean matches(PsiFile file);
}
