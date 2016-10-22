package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;

import java.util.Optional;

public interface RelativeDirectoryResolver {
    Optional<PsiDirectory> resolve(PsiElement element);
}
