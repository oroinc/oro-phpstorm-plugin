package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.oroplatform.idea.oroplatform.PhpClassUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Stream;

import static com.oroplatform.idea.oroplatform.Functions.toStream;
import static java.util.Arrays.asList;

public class PhpCallbackReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final boolean methodExists;
    private final String methodName;
    private final String className;
    private final PhpIndex phpIndex;
    private static final ComposedInsertHandler INSERT_HANDLER =
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

        return Stream.concat(
            phpClasses.stream().map(PsiElementResolveResult::new),
            phpClasses.stream()
                .flatMap(phpClass -> toStream(phpClass.findMethodByName(methodName)))
                .filter(phpMethod -> phpMethod.isStatic() && phpMethod.getAccess() == PhpModifier.Access.PUBLIC)
                .map(PsiElementResolveResult::new)
        ).toArray(ResolveResult[]::new);
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

        return phpClasses.stream()
            .flatMap(phpClass -> phpClass.getMethods().stream())
            .filter(phpMethod -> phpMethod.getAccess() == PhpModifier.Access.PUBLIC && phpMethod.isStatic() && matcher.prefixMatches(phpMethod.getName()))
            .map(PhpMethodLookupElement::new)
            .toArray();
    }

    @NotNull
    private Object[] getClassVariants() {
        final PrefixMatcher classMatcher = new CamelHumpMatcher(className);

        return phpIndex.getAllClassNames(classMatcher).stream()
            .flatMap(className -> phpIndex.getClassesByName(className).stream())
            .filter(phpClass -> phpClass.hasStaticMembers() && !PhpClassUtil.isTestOrGeneratedClass(phpClass.getPresentableFQN()))
            .map(phpClass -> new PhpClassLookupElement(phpClass, true, INSERT_HANDLER))
            .toArray();
    }

}
