package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

public class PhpFieldReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final Collection<String> classNames = new LinkedList<>();
    private final String fieldName;

    public PhpFieldReference(@NotNull PsiElement element, Collection<String> classNames, String fieldName) {
        super(element);
        this.classNames.addAll(classNames);
        this.fieldName = fieldName;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());

        return getClasses(phpIndex).stream()
            .flatMap(phpClass -> toStream(() -> phpClass.findFieldByName(fieldName, false)))
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    @NotNull
    private Collection<PhpClass> getClasses(PhpIndex phpIndex) {
        return classNames.stream()
            .flatMap(className -> {
                if(className.contains(":") && className.length() > className.indexOf(":") + 1) {
                    final PrefixMatcher matcher = new CamelHumpMatcher(className.replace(":", ""));
                    final String classSimpleName = className.split(":")[1];
                    final Collection<PhpClass> phpClasses = phpIndex.getClassesByName(classSimpleName);

                    return phpClasses.stream()
                        .filter(phpClass -> matcher.isStartMatch(phpClass.getFQN().replace("\\", "")));
                } else {
                    return phpIndex.getClassesByFQN(className).stream();
                }
            })
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());

        return getClasses(phpIndex).stream()
            .flatMap(phpClass -> phpClass.getFields().stream())
            .map(PhpLookupElement::new)
            .toArray();
    }
}
