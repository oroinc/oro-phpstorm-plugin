package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider.FilePathReferenceProvider;
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
    public PsiReferenceProvider filePath(InsertHandler<LookupElement> insertHandler) {
        return new FilePathReferenceProvider();
    }

    @Override
    public PsiReferenceProvider filePathIn(String relativeToAppDir, InsertHandler<LookupElement> insertHandler) {
        return new FilePathReferenceProvider(relativeToAppDir);
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
    public PsiReferenceProvider twigTemplate(InsertHandler<LookupElement> insertHandler) {
        return new TwigTemplateReferenceProvider();
    }

    @Override
    public PsiReferenceProvider route(InsertHandler<LookupElement> insertHandler) {
        return new RouteReferenceProvider();
    }
}
