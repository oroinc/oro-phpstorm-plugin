package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpClassLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class PhpClassReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String text;

    public PhpClassReference(PsiElement psiElement, @NotNull String text) {
        super(psiElement);
        this.text = text.replace("IntellijIdeaRulezzz", "").trim().replace("\\\\", "\\");
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());

        List<ResolveResult> results = new LinkedList<ResolveResult>();

        for(PhpClass phpClass : phpIndex.getClassesByFQN(text)) {
            results.add(new PsiElementResolveResult(phpClass));
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());

        List<PhpClassLookupElement> results = new LinkedList<PhpClassLookupElement>();
        PrefixMatcher classMatcher = new CamelHumpMatcher(text);
        for(String className : phpIndex.getAllClassNames(classMatcher)) {
            PhpClass phpClass = phpIndex.getClassByName(className);
            if(phpClass != null) {
                results.add(new PhpClassLookupElement(phpClass, true, PhpClassInsertHandler.INSTANCE));
            }
        }

        return results.toArray();
    }
}
