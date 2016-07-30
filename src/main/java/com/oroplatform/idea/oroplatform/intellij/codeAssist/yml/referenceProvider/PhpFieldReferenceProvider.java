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
import org.jetbrains.yaml.psi.YAMLPsiElement;
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
        if(element instanceof YAMLPsiElement) {
            final YAMLFile file = (YAMLFile) element.getContainingFile();
            final Set<PsiElement> ancestors = getAncestors(element);
            final Collection<String> properties = classPropertyPath.doesPointToValue() ?
                getPropertyValuesFrom(classPropertyPath, YamlPsiElements.getMappingsFrom(file), ancestors) :
                getPropertyKeysFrom(classPropertyPath, YamlPsiElements.getMappingsFrom(file), ancestors);
            if(!properties.isEmpty()) {
                return new PsiReference[]{new PhpFieldReference(getReferenceElement(element), properties.iterator().next(), getReferenceText(element))};
            }
        }

        return new PsiReference[0];
    }

    private static PsiElement getReferenceElement(PsiElement element) {
        return element instanceof YAMLKeyValue ? ((YAMLKeyValue) element).getKey() : element;
    }

    private static String getReferenceText(PsiElement element) {
        if(element instanceof YAMLScalar) {
            return ((YAMLScalar) element).getTextValue();
        } else if(element instanceof YAMLKeyValue) {
            return ((YAMLKeyValue) element).getKeyText();
        } else {
            return element.getText();
        }
    }
}
