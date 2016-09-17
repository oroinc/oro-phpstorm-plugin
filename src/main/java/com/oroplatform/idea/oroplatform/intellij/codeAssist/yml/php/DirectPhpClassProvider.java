package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.php;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Collection;
import java.util.Set;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.*;

class DirectPhpClassProvider implements PhpClassProvider {
    public Collection<String> getPhpClasses(PhpIndex phpIndex, PsiElement element, PropertyPath propertyPath) {
        final YAMLFile file = (YAMLFile) element.getContainingFile();
        final Set<PsiElement> ancestors = getAncestors(element);
        return getPropertyFrom(propertyPath, YamlPsiElements.getMappingsFrom(file), ancestors);
    }
}
