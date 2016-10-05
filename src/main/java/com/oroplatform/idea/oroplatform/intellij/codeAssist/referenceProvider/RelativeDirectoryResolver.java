package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;

public interface RelativeDirectoryResolver {
    PsiDirectory resolve(PsiElement element);
}
