package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.schema.Scalar;

public interface ReferenceProviders {
    PsiReferenceProvider filePath(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpCallback(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpClass(Scalar.PhpClass phpClass, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpField(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpMethod(String pattern, InsertHandler<LookupElement> insertHandler);
}
