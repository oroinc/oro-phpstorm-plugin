package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider.PhpClassReferenceProvider;
import com.oroplatform.idea.oroplatform.schema.Scalar;

public class PhpClassReferenceProviders implements ReferenceProviders {
    private final Scalar.PhpClass phpClass;

    public PhpClassReferenceProviders(Scalar.PhpClass phpClass) {
        this.phpClass = phpClass;
    }

    @Override
    public PsiReferenceProvider forYaml(InsertHandler<LookupElement> insertHandler) {
        return new PhpClassReferenceProvider(phpClass, insertHandler);
    }
}
