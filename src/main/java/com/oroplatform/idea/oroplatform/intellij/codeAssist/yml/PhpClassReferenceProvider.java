package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassReference;
import com.oroplatform.idea.oroplatform.schema.Scalar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

class PhpClassReferenceProvider extends PsiReferenceProvider {

    static {
        ElementManipulators.INSTANCE.addExplicitExtension(YAMLKeyValue.class, new YamlKeyValueManipulator());
    }

    private final Scalar.PhpClass phpClass;

    public PhpClassReferenceProvider(Scalar.PhpClass phpClass) {
        this.phpClass = phpClass;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLKeyValue) {
            return new PsiReference[] { new PhpClassReference(element, phpClass.getType(), ((YAMLKeyValue) element).getValueText()) };
        } else if(element instanceof YAMLScalar) {
            return new PsiReference[] { new PhpClassReference(element, phpClass.getType(), ((YAMLScalar) element).getTextValue()) };
        } else {
            return new PsiReference[] { new PhpClassReference(element, phpClass.getType(), element.getText()) };
        }
    }
}
