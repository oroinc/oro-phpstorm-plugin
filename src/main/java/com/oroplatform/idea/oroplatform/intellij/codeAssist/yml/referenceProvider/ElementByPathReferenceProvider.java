package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.ElementByPathReference;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLScalar;

public class ElementByPathReferenceProvider extends PsiReferenceProvider {
    private final PropertyPath path;
    private final String prefix;
    private final InsertHandler<LookupElement> insertHandler;

    public ElementByPathReferenceProvider(PropertyPath path, String prefix, InsertHandler<LookupElement> insertHandler) {
        this.path = path;
        this.prefix = prefix;
        this.insertHandler = insertHandler;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLScalar) {
            return new PsiReference[] {
                new ElementByPathReference(element, path, prefix, ((YAMLScalar) element).getTextValue(), insertHandler)
            };
        }
        return new PsiReference[0];
    }
}
