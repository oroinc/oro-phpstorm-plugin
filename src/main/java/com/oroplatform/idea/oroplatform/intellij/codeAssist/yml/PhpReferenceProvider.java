package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLQuotedText;

class PhpReferenceProvider extends PsiReferenceProvider {

    static {
        ElementManipulators.INSTANCE.addExplicitExtension(YAMLKeyValue.class, new YamlKeyValueManipulator());
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLKeyValue) {
            return new PsiReference[] { new PhpClassReference(element, ((YAMLKeyValue) element).getValueText()) };
        } else if(element instanceof YAMLQuotedText) {
            return new PsiReference[] { new PhpClassReference(element, ((YAMLQuotedText) element).getTextValue()) };
        }

        return new PsiReference[0];
    }
}
