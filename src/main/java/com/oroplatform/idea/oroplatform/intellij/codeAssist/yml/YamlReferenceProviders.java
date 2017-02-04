package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.*;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider.FilePathReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider.RelativeDirectoryResolver;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider.RequirejsReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.php.YamlPhpClassProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider.*;
import com.oroplatform.idea.oroplatform.schema.PhpClass;
import com.oroplatform.idea.oroplatform.schema.PhpMethod;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

class YamlReferenceProviders implements ReferenceProviders {
    @Override
    public PhpClassProviders phpClassProviders() {
        return new YamlPhpClassProviders();
    }

    @Override
    public PsiReferenceProvider filePath(RelativeDirectoryResolver relativeDirectoryResolver, int allowedDepth, InsertHandler<LookupElement> insertHandler) {
        return new FilePathReferenceProvider(relativeDirectoryResolver, allowedDepth);
    }

    @Override
    public PsiReferenceProvider file(RootDirsFinder rootDirsFinder, StringWrapperProvider stringWrapperProvider, VirtualFileFilter virtualFileFilter, InsertHandler<LookupElement> insertHandler) {
        return new WrappedFileReferenceProvider(stringWrapperProvider, rootDirsFinder, virtualFileFilter, insertHandler);
    }

    @Override
    public PsiReferenceProvider phpCallback(InsertHandler<LookupElement> insertHandler) {
        return new PhpCallbackReferenceProvider();
    }

    @Override
    public PsiReferenceProvider phpCallable(InsertHandler<LookupElement> insertHandler) {
        return new PhpCallableReferenceProvider();
    }

    @Override
    public PsiReferenceProvider phpClass(PhpClass phpClass, InsertHandler<LookupElement> insertHandler) {
        return new PhpClassReferenceProvider(phpClass, insertHandler);
    }

    @Override
    public PsiReferenceProvider phpField(PropertyPath classPropertyPath, PhpClassProvider phpClassProvider, InsertHandler<LookupElement> insertHandler) {
        return new PhpFieldReferenceProvider(classPropertyPath, phpClassProvider);
    }

    @Override
    public PsiReferenceProvider phpMethod(String pattern, InsertHandler<LookupElement> insertHandler) {
        return new PhpMethodReferenceProvider(new PhpMethod(pattern));
    }

    @Override
    public PsiReferenceProvider twigTemplate(InsertHandler<LookupElement> insertHandler, String pattern) {
        return new TwigTemplateReferenceProvider(pattern);
    }

    @Override
    public PsiReferenceProvider route(InsertHandler<LookupElement> insertHandler) {
        return new RouteReferenceProvider();
    }

    @Override
    public PsiReferenceProvider resource(String pattern, InsertHandler<LookupElement> insertHandler) {
        return new ResourceReferenceProvider(pattern);
    }

    @Override
    public PsiReferenceProvider requirejs(InsertHandler<LookupElement> insertHandler) {
        return RequirejsReferenceProvider.instance();
    }

    @Override
    public PsiReferenceProvider workflowScope(InsertHandler<LookupElement> insertHandler) {
        return new WorkflowScopeReferenceProvider(insertHandler);
    }

    @Override
    public PsiReferenceProvider translation(InsertHandler<LookupElement> insertHandler) {
        return new TranslationReferenceProvider(insertHandler);
    }
}
