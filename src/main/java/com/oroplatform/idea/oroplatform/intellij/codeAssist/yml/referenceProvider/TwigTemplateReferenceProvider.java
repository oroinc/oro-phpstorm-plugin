package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ResourceReference;
import com.oroplatform.idea.oroplatform.symfony.Resource;
import com.oroplatform.idea.oroplatform.symfony.TwigTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Collections;

public class TwigTemplateReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLKeyValue) {
            return new PsiReference[] { new ResourceReference(element, ((YAMLKeyValue) element).getValueText(), "twig", TwigTemplateReferenceProvider::renderTemplate, Collections.singletonList("views")) };
        }

        return new PsiReference[0];
    }

    private static String renderTemplate(Resource resource) {
        return TwigTemplate.from(resource).map(TwigTemplate::getName).orElse("");
    }
}
