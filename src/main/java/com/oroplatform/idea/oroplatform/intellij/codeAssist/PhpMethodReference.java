package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PhpMethodReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String className;
    private final String methodName;

    public PhpMethodReference(PsiElement psiElement, String className, String methodName) {
        super(psiElement);
        this.className = className.replace("IntellijIdeaRulezzz", "").trim().replace("\\\\", "\\");
        this.methodName = methodName.replace("IntellijIdeaRulezzz", "").trim();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());

        Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN(className);

        List<ResolveResult> result = new LinkedList<ResolveResult>();

        for(PhpClass phpClass : phpClasses) {
            Method method = phpClass.findMethodByName(methodName);
            if(method != null) {
                result.add(new PsiElementResolveResult(method));
            }
        }

        return result.toArray(new ResolveResult[result.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());
        Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN(className);

        List<PhpLookupElement> methods = new LinkedList<PhpLookupElement>();

        for(PhpClass phpClass : phpClasses) {
            for(Method method : phpClass.getMethods()) {
                if(method.getAccess() == PhpModifier.Access.PUBLIC) {
                    methods.add(new PhpLookupElement(method));
                }
            }
        }

        return methods.toArray();
    }
}
