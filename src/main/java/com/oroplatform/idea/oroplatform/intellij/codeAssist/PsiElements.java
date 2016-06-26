package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class PsiElements {
    public final static String IN_PROGRESS_VALUE = "IntellijIdeaRulezzz";

    @NotNull
    public static PsiElementPattern.Capture<PsiFile> fileInProjectWithPluginEnabled() {
        return psiElement(PsiFile.class).with(new PatternCondition<PsiFile>(null) {
            @Override
            public boolean accepts(@NotNull PsiFile psiFile, ProcessingContext context) {
                return OroPlatformSettings.getInstance(psiFile.getProject()).isPluginEnabled();
            }
        });
    }
}
