package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

import java.util.Collection;

public interface PhpClassProvider {
    Collection<String> getPhpClasses(PhpIndex phpIndex, PsiElement element, PropertyPath propertyPath);
}
