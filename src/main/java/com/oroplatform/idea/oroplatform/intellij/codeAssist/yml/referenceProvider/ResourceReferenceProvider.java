package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ResourceReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLScalar;

public class ResourceReferenceProvider extends PsiReferenceProvider {
    private final String pattern;

    public ResourceReferenceProvider(String pattern) {
        this.pattern = pattern;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLScalar) {
            return new PsiReference[]{ new ResourceReference(element, ((YAMLScalar) element).getTextValue(), pattern)};
        }

        return new PsiReference[0];
    }
}
