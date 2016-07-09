package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.oroplatform.idea.oroplatform.PhpClassUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

public class PhpCallbackReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final boolean methodExists;
    private final String methodName;
    private final String className;
    private final PhpIndex phpIndex;
    private static ComposedInsertHandler INSERT_HANDLER =
        new ComposedInsertHandler(asList(PhpClassInsertHandler.INSTANCE, new ColonAppenderInsertHandler(), new AutoPopupInsertHandler()));

    public PhpCallbackReference(PsiElement element, String text) {
        super(element);
        this.methodExists = text.contains("::");
        final String[] parts = text.split("::");
        this.methodName = parts.length > 1 ? parts[1].replace(PsiElements.IN_PROGRESS_VALUE, "").trim() : null;
        this.className = parts.length > 0 ? parts[0].replace(PsiElements.IN_PROGRESS_VALUE, "").trim().replace("\\\\", "\\") : null;
        this.phpIndex = PhpIndex.getInstance(getElement().getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN(className);

        final Collection<ResolveResult> results = new LinkedList<ResolveResult>();

        for (PhpClass phpClass : phpClasses) {
            results.add(new PsiElementResolveResult(phpClass));
        }

        if(methodExists) {
            for (PhpClass phpClass : phpClasses) {
                final Method phpMethod = phpClass.findMethodByName(methodName);
                if(phpMethod != null && phpMethod.isStatic() && phpMethod.getAccess() == PhpModifier.Access.PUBLIC) {
                    results.add(new PsiElementResolveResult(phpMethod));
                }
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        if(methodExists) {
            return getMethodVariants();
        } else {
            return getClassVariants();
        }
    }

    @NotNull
    private Object[] getMethodVariants() {
        final PrefixMatcher matcher = new CamelHumpMatcher(methodName);

        final Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN(className);

        final Collection<Object> results = new LinkedList<Object>();

        for (PhpClass phpClass : phpClasses) {
            for (Method phpMethod : phpClass.getMethods()) {
                if(phpMethod.getAccess() == PhpModifier.Access.PUBLIC && phpMethod.isStatic() && matcher.prefixMatches(phpMethod.getName())) {
                    results.add(new PhpMethodLookupElement(phpMethod));
                }
            }
        }

        return results.toArray();
    }

    @NotNull
    private Object[] getClassVariants() {
        final List<LookupElement> results = new LinkedList<LookupElement>();
        final PrefixMatcher classMatcher = new CamelHumpMatcher(className);

        for (String className : phpIndex.getAllClassNames(classMatcher)) {
            for (PhpClass phpClass : phpIndex.getClassesByName(className)) {
                if(phpClass.hasStaticMembers() && !PhpClassUtil.isTestOrGeneratedClass(phpClass.getPresentableFQN())) {
                    results.add(new PhpClassLookupElement(phpClass, true, INSERT_HANDLER));
                }
            }
        }

        return results.toArray(new LookupElement[results.size()]);
    }

}
