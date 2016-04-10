package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpFieldReference;
import com.oroplatform.idea.oroplatform.schema.Scalar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.PsiElements.getYamlKeyValueSiblingWithName;

class PhpFieldReferenceProvider extends PsiReferenceProvider {
    private final Scalar.PhpField phpField;

    PhpFieldReferenceProvider(Scalar.PhpField phpField) {
        this.phpField = phpField;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

        if(element instanceof YAMLKeyValue) {
            YAMLKeyValue classKeyValue = getYamlKeyValueSiblingWithName((YAMLKeyValue) element, "entity");
            String className = classKeyValue == null ? "" : classKeyValue.getValueText();
            return new PsiReference[]{new PhpFieldReference(element, phpField, className, ((YAMLKeyValue) element).getValueText())};
        }
        return new PsiReference[0];
    }
}
