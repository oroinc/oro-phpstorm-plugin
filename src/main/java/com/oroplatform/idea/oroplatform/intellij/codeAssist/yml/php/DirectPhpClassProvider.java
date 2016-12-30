package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.php;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassProvider;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

import java.util.Collection;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getPropertyFrom;

class DirectPhpClassProvider implements PhpClassProvider {
    public Collection<String> getPhpClasses(PhpIndex phpIndex, PsiElement element, PropertyPath propertyPath) {
        return getPropertyFrom(propertyPath, element);
    }
}
