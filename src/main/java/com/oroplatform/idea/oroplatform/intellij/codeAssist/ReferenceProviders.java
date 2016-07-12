package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiReferenceProvider;

public interface ReferenceProviders {
    PsiReferenceProvider forYaml(InsertHandler<LookupElement> insertHandler);
}
