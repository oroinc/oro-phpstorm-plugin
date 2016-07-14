package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpMethodReference;
import com.oroplatform.idea.oroplatform.schema.PhpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getYamlKeyValueSiblingWithName;

public class PhpMethodReferenceProvider extends PsiReferenceProvider {
    private final PhpMethod phpMethod;

    public PhpMethodReferenceProvider(PhpMethod method) {
        phpMethod = method;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLKeyValue) {
            YAMLKeyValue classKeyValue = getYamlKeyValueSiblingWithName((YAMLKeyValue) element, "class");
            String className = classKeyValue == null ? "" : classKeyValue.getValueText();
            return new PsiReference[]{new PhpMethodReference(element, phpMethod, className, ((YAMLKeyValue) element).getValueText())};
        }
        return new PsiReference[0];
    }

}
