package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.psi.*;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import com.oroplatform.idea.oroplatform.schema.PhpMethod;
import com.oroplatform.idea.oroplatform.symfony.Service;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ServiceMethodReference extends PsiPolyVariantReferenceBase<PsiElement> {

    private final Optional<PsiPolyVariantReference> innerReference;

    public ServiceMethodReference(PsiElement element, String serviceId, String methodName) {
        super(element);

        this.innerReference = ServicesIndex.instance(element.getProject()).findService(serviceId)
            .flatMap(Service::getClassName)
            .map(className -> new PhpMethodReference(element, new PhpMethod(new PhpMethod.PhpMethodNonStaticMatcher()), className, methodName));
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return innerReference.map(ref -> ref.multiResolve(incompleteCode)).orElse(new ResolveResult[0]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return innerReference.map(PsiReference::getVariants).orElse(new Object[0]);
    }
}
