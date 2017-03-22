package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class IfCompositeConditionReferenceProvider extends PsiReferenceProvider {
    private static final Set<PsiElement> beingComputing = new HashSet<>();
    private static final String ABSTRACT_COMPOSITE_FQN = "Oro\\Component\\ConfigExpression\\Condition\\AbstractComposite";

    private final PsiReferenceProvider provider;

    public IfCompositeConditionReferenceProvider(PsiReferenceProvider provider) {
        this.provider = provider;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return Stream.of(provider.getReferencesByElement(element, context))
            .map(ConditionalReference::new)
            .toArray(PsiReference[]::new);
    }

    private static class ConditionalReference extends PsiReferenceWrapper implements PsiPolyVariantReference {
        private final PsiReference reference;
        private Boolean isComposed;

        private ConditionalReference(PsiReference reference) {
            super(reference);
            this.reference = reference;
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            return reference instanceof PsiPolyVariantReference ? ((PsiPolyVariantReference) reference).multiResolve(incompleteCode) : new ResolveResult[0];
        }

        private boolean isComposed() {
            if(beingComputing.contains(reference.getElement())) return false;
            if(isComposed == null) {
                try {
                    beingComputing.add(reference.getElement());
                    isComposed = YamlPsiElements.getFirstPhpClassKeyFromAncestors(reference.getElement(), keyValue -> keyValue.getKeyText().contains("@"))
                        .anyMatch(phpClass -> Stream.of(phpClass.getSupers()).anyMatch(superClass -> ABSTRACT_COMPOSITE_FQN.equals(superClass.getPresentableFQN())));
                } finally {
                    beingComputing.remove(reference.getElement());
                }
            }
            return isComposed;
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            return isComposed() ? reference.resolve() : null;
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return isComposed() ? reference.getVariants() : new Object[0];
        }
    }
}
