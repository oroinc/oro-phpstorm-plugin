package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.AppRelativeRootDirFinder;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RootDirFinder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RelativeToAppDirectoryResolver implements RelativeDirectoryResolver {
    private final RootDirFinder rootDirFinder;

    public RelativeToAppDirectoryResolver(@NotNull String relativeToAppDir) {
        this.rootDirFinder = new AppRelativeRootDirFinder(relativeToAppDir);
    }

    @Override
    public Optional<PsiDirectory> resolve(PsiElement element) {
        final Project project = element.getProject();
        return rootDirFinder.getRootDir(element)
            .flatMap(rootDir -> Optional.ofNullable(PsiManager.getInstance(project).findDirectory(rootDir)));
    }
}
