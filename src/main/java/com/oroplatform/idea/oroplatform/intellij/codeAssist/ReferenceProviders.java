package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider.RelativeDirectoryResolver;
import com.oroplatform.idea.oroplatform.schema.PhpClass;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

public interface ReferenceProviders {

    PhpClassProviders phpClassProviders();

    PsiReferenceProvider filePath(RelativeDirectoryResolver relativeDirectoryResolver, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider filePath(RelativeDirectoryResolver relativeDirectoryResolver, int allowedDepth, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider file(RootDirsFinder rootDirsFinder, StringWrapperProvider stringWrapperProvider, VirtualFileFilter virtualFileFilter, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpCallback(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpCallable(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpClass(PhpClass phpClass, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpField(PropertyPath propertyPath, PhpClassProvider phpClassProvider, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpMethod(String pattern, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider twigTemplate(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider route(InsertHandler<LookupElement> insertHandler);
}
