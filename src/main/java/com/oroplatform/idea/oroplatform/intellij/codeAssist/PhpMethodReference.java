package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.oroplatform.idea.oroplatform.schema.PhpMethod;
import org.jetbrains.annotations.NotNull;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

public class PhpMethodReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String className;
    private final String methodName;
    private final PhpMethod phpMethod;

    public PhpMethodReference(PsiElement psiElement, PhpMethod phpMethod, String className, String methodName) {
        super(psiElement);
        this.phpMethod = phpMethod;
        this.className = className.replace(PsiElements.IN_PROGRESS_VALUE, "").trim().replace("\\\\", "\\");
        this.methodName = methodName.replace(PsiElements.IN_PROGRESS_VALUE, "").trim();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());

        return phpIndex.getClassesByFQN(className).stream()
            .flatMap(phpClass -> toStream(() -> phpClass.findMethodByName(methodName)))
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());

        return phpIndex.getClassesByFQN(className).stream()
            .flatMap(phpClass -> phpClass.getMethods().stream())
            .filter(method -> method.getAccess() == PhpModifier.Access.PUBLIC && phpMethod.matches(method))
            .map(PhpLookupElement::new)
            .toArray();
    }
}
