package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.oroplatform.idea.oroplatform.Functions.toStream;

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

    public static <T extends PsiElement> Function<PsiElement, Stream<T>> elementFilter(Class<T> cls) {
        return element -> toStream(Optional.ofNullable(element))
            .filter(e -> cls.isAssignableFrom(e.getClass()))
            .map(cls::cast);
    }
}
