package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.psi.*;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.oroplatform.idea.oroplatform.schema.Scalar;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PhpFieldReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final Scalar.PhpField phpField;
    private final String className;
    private final String fieldName;

    public PhpFieldReference(PsiElement element, Scalar.PhpField phpField, String className, String fieldName) {
        super(element);
        this.phpField = phpField;
        this.className = className;
        this.fieldName = fieldName;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());
        final Collection<PhpClass> phpClasses = getClasses(phpIndex);

        final List<ResolveResult> result = new LinkedList<ResolveResult>();

        for(PhpClass phpClass : phpClasses) {
            Field field = phpClass.findFieldByName(fieldName, false);

            if(field != null) {
                result.add(new PsiElementResolveResult(field));
            }
        }

        return result.toArray(new ResolveResult[result.size()]);
    }

    @NotNull
    private Collection<PhpClass> getClasses(PhpIndex phpIndex) {
        if(className.contains(":") && className.length() > className.indexOf(":") + 1) {
            final PrefixMatcher matcher = new CamelHumpMatcher(className.replace(":", ""));
            final String classSimpleName = className.split(":")[1];
            final Collection<PhpClass> phpClasses = phpIndex.getClassesByName(classSimpleName);
            final Collection<PhpClass> result = new LinkedList<PhpClass>();

            for(PhpClass phpClass : phpClasses) {
                if(matcher.isStartMatch(phpClass.getFQN().replace("\\", ""))) {
                    result.add(phpClass);
                }
            }

            return result;
        } else {
            return phpIndex.getClassesByFQN(className);
        }
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());
        final Collection<PhpClass> phpClasses = getClasses(phpIndex);

        final List<PhpLookupElement> result = new LinkedList<PhpLookupElement>();
        for(PhpClass phpClass : phpClasses) {
            for(Field field : phpClass.getFields()) {
                result.add(new PhpLookupElement(field));
            }

        }

        return result.toArray();
    }
}
