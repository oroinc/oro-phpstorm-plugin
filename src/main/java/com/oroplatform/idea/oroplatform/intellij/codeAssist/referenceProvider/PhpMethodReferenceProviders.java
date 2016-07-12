package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider.PhpMethodReferenceProvider;
import com.oroplatform.idea.oroplatform.schema.Scalar;

public class PhpMethodReferenceProviders implements ReferenceProviders {
    private final String pattern;

    public PhpMethodReferenceProviders(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public PsiReferenceProvider forYaml(InsertHandler<LookupElement> insertHandler) {
        return new PhpMethodReferenceProvider(new Scalar.PhpMethod(pattern));
    }
}
