package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpFieldReference;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.Collection;
import java.util.Set;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.*;

public class PhpFieldReferenceProvider extends PsiReferenceProvider {
    private final PropertyPath classPropertyPath;

    public PhpFieldReferenceProvider(PropertyPath classPropertyPath) {

        this.classPropertyPath = classPropertyPath;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

        //TODO: refactor this class...
        if(element instanceof YAMLKeyValue) {
            if(classPropertyPath.getProperties().isEmpty()) {
                //TODO: refactor this - support for classPropertyPath. Add support for sequences in classPropertyPath
                final YAMLKeyValue classKeyValue = getYamlKeyValueSiblingWithName((YAMLKeyValue) element, "entity");
                final String className = classKeyValue == null ? "" : classKeyValue.getValueText();
                return new PsiReference[]{new PhpFieldReference(element, className, ((YAMLKeyValue) element).getValueText())};
            } else {
                final YAMLFile file = (YAMLFile) element.getContainingFile();
                final Set<PsiElement> ancestors = getAncestors(element);
                final Collection<String> properties = getPropertyNamesFrom(classPropertyPath, YamlPsiElements.getMappingsFrom(file), ancestors);
                if(!properties.isEmpty()) {
                    return new PsiReference[]{new PhpFieldReference(((YAMLKeyValue) element).getKey(), properties.iterator().next(), ((YAMLKeyValue) element).getKeyText())};
                }
            }
        } else if(element instanceof YAMLScalar) {
            final YAMLFile file = (YAMLFile) element.getContainingFile();
            final Set<PsiElement> ancestors = getAncestors(element);
            final Collection<String> properties = getPropertyNamesFrom(classPropertyPath, YamlPsiElements.getMappingsFrom(file), ancestors);
            if(!properties.isEmpty()) {
                return new PsiReference[]{new PhpFieldReference(element, properties.iterator().next(), ((YAMLScalar) element).getTextValue())};
            }
        }
        return new PsiReference[0];
    }
}
