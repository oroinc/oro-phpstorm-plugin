package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ResourceReference;
import com.oroplatform.idea.oroplatform.symfony.Resource;
import com.oroplatform.idea.oroplatform.symfony.TwigTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLScalar;

import java.util.Collections;

public class TwigTemplateReferenceProvider extends PsiReferenceProvider {
    private final String pattern;

    public TwigTemplateReferenceProvider(String pattern) {
        this.pattern = pattern;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if(element instanceof YAMLScalar) {
            return new PsiReference[] { new ResourceReference(element, ((YAMLScalar) element).getTextValue(), pattern+".twig", TwigTemplateReferenceProvider::renderTemplate, Collections.singletonList("views")) };
        }

        return new PsiReference[0];
    }

    private static String renderTemplate(Resource resource) {
        return TwigTemplate.from(resource).map(TwigTemplate::getName).orElse("");
    }
}
