package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.AppRelativeRootDirsFinder;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RootDirsFinder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RelativeToAppDirectoryResolver implements RelativeDirectoryResolver {
    private final RootDirsFinder rootDirsFinder;

    public RelativeToAppDirectoryResolver(@NotNull String relativeToAppDir) {
        this.rootDirsFinder = new AppRelativeRootDirsFinder(relativeToAppDir);
    }

    @Override
    public Optional<PsiDirectory> resolve(PsiElement element) {
        final Project project = element.getProject();
        return rootDirsFinder.getRootDirs(element).stream().findFirst()
            .flatMap(rootDir -> Optional.ofNullable(PsiManager.getInstance(project).findDirectory(rootDir)));
    }
}
