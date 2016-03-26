package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpMethodReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLKeyValue;

class PhpMethodReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLKeyValue) {
            YAMLKeyValue classKeyValue = getYamlKeyValueSiblingWithName((YAMLKeyValue) element, "class");
            String className = classKeyValue == null ? "" : classKeyValue.getValueText();
            return new PsiReference[]{new PhpMethodReference(element, className, ((YAMLKeyValue) element).getValueText())};
        }
        return new PsiReference[0];
    }

    @Nullable
    private static YAMLKeyValue getYamlKeyValueSiblingWithName(YAMLKeyValue keyValue, String name) {
        for(PsiElement element : keyValue.getParent().getChildren()) {
            if(element instanceof YAMLKeyValue && name.equals(((YAMLKeyValue) element).getKeyText())) {
                return ((YAMLKeyValue) element);
            }
        }

        return null;
    }
}
