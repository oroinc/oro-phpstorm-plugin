package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassReference;
import com.oroplatform.idea.oroplatform.schema.Scalar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;

class PhpClassReferenceProvider extends PsiReferenceProvider {

    static {
        ElementManipulators.INSTANCE.addExplicitExtension(YAMLKeyValue.class, new YamlKeyValueManipulator());
        ElementManipulators.INSTANCE.addExplicitExtension(LeafPsiElement.class, new LeafPsiElementManipulator());
    }

    private final Scalar.PhpClass phpClass;
    private final InsertHandler<LookupElement> insertHandler;

    public PhpClassReferenceProvider(Scalar.PhpClass phpClass, InsertHandler<LookupElement> insertHandler) {
        this.phpClass = phpClass;
        this.insertHandler = insertHandler;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLKeyValue) {
            return new PsiReference[] {
                new PhpClassReference(((YAMLKeyValue) element).getKey(), phpClass, ((YAMLKeyValue) element).getKeyText(), insertHandler)
            };

        // skip scalar element in key context
        } else if(element instanceof YAMLScalar && context.get("key") == null) {
            return new PsiReference[]{new PhpClassReference(element, phpClass, ((YAMLScalar) element).getTextValue(), insertHandler)};
        }

        return new PsiReference[0];
    }
}
