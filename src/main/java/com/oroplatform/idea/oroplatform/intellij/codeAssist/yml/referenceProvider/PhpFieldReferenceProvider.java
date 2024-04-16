package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpFieldReference;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpClassProvider;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLPsiElement;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.Collection;

public class PhpFieldReferenceProvider extends PsiReferenceProvider {
    private final PropertyPath classPropertyPath;
    private final PhpClassProvider phpClassProvider;

    public PhpFieldReferenceProvider(PropertyPath classPropertyPath, PhpClassProvider phpClassProvider) {
        this.classPropertyPath = classPropertyPath;
        this.phpClassProvider = phpClassProvider;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        try {
            final boolean allowsKey = context.get("key") != null;
            if(!allowsKey && element instanceof YAMLKeyValue) return new PsiReference[0];

            if(element instanceof YAMLPsiElement) {
                final PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());
                final Collection<String> properties = phpClassProvider.getPhpClasses(phpIndex, element, classPropertyPath);

                if(!properties.isEmpty()) {
                    return new PsiReference[]{new PhpFieldReference(getReferenceElement(element, allowsKey), properties, getReferenceText(element, allowsKey))};
                }
            }
        } catch (Throwable throwable) {
               // TODO handle exception
        }

        return new PsiReference[0];
    }

    private static @NotNull PsiElement getReferenceElement(PsiElement element, boolean allowsKey) {
        if(element instanceof YAMLKeyValue keyValue) {
            assert (allowsKey ? keyValue.getKey() : keyValue.getValue()) != null;
            assert keyValue.getValue() != null;
            assert allowsKey;
            return element;
        } else {
            return element;
        }
    }

    private static String getReferenceText(PsiElement element, boolean allowsKey) {
        if(element instanceof YAMLScalar) {
            return ((YAMLScalar) element).getTextValue();
        } else if(element instanceof YAMLKeyValue) {
            final YAMLKeyValue keyValue = (YAMLKeyValue) element;
            return allowsKey ? keyValue.getKeyText() : keyValue.getValueText();
        } else {
            return element.getText();
        }
    }
}
