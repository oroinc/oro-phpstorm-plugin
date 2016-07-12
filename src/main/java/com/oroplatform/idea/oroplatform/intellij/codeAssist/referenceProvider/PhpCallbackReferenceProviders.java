package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider.PhpCallbackReferenceProvider;

public class PhpCallbackReferenceProviders implements ReferenceProviders {
    @Override
    public PsiReferenceProvider forYaml(InsertHandler<LookupElement> insertHandler) {
        return new PhpCallbackReferenceProvider();
    }
}
