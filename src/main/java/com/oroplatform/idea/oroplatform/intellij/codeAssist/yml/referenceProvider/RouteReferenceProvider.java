package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RouteReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

public class RouteReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLScalar) {
            return new PsiReference[] { new RouteReference(element, ((YAMLScalar) element).getTextValue()) };
        } else if(element instanceof YAMLKeyValue && context.get("key") != null) {
            final YAMLKeyValue keyValue = (YAMLKeyValue) element;
            return new PsiReference[] { new RouteReference(element, keyValue.getKeyText()) };
        }

        return new PsiReference[0];
    }
}
