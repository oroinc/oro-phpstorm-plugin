package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider.RelativeDirectoryResolver;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import com.oroplatform.idea.oroplatform.schema.PhpClass;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public interface ReferenceProviders {

    PhpClassProviders phpClassProviders();

    PsiReferenceProvider filePath(RelativeDirectoryResolver relativeDirectoryResolver, int allowedDepth, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider file(RootDirsFinder rootDirsFinder, StringWrapperProvider stringWrapperProvider, VirtualFileFilter virtualFileFilter, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpCallback(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpCallable(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpClass(PhpClass phpClass, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpField(PropertyPath propertyPath, PhpClassProvider phpClassProvider, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider phpMethod(String pattern, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider twigTemplate(InsertHandler<LookupElement> insertHandler, String pattern);
    PsiReferenceProvider route(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider resource(String pattern, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider requirejs(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider workflowScope(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider translation(InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider propertiesFromPath(PropertyPath path, String prefix, InsertHandler<LookupElement> insertHandler);
    PsiReferenceProvider serviceAlias(String aliasTag, InsertHandler<LookupElement> insertHandler, Function<ServicesIndex, Optional<Collection<String>>> getAllowedValues, String prefix);
    PsiReferenceProvider ifCompositeCondition(PsiReferenceProvider provider);

    default PsiReferenceProvider serviceAlias(String aliasTag, InsertHandler<LookupElement> insertHandler) {
        return serviceAlias(aliasTag, insertHandler, servicesIndex -> Optional.empty(), "");
    }

    default PsiReferenceProvider serviceAlias(String aliasTag, InsertHandler<LookupElement> insertHandler, String prefix) {
        return serviceAlias(aliasTag, insertHandler, servicesIndex -> Optional.empty(), prefix);
    }
}
