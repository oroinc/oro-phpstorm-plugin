package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.oroplatform.idea.oroplatform.symfony.Bundle;
import com.oroplatform.idea.oroplatform.symfony.Bundles;
import com.oroplatform.idea.oroplatform.symfony.Resource;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

public class ResourceReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String resourceName;
    private final String extension;
    private final PhpIndex phpIndex;
    private final Bundles bundles;

    public ResourceReference(PsiElement psiElement, String resourceName, String extension) {
        super(psiElement);
        this.resourceName = resourceName;
        this.extension = extension;

        this.phpIndex = PhpIndex.getInstance(psiElement.getProject());
        this.bundles = new Bundles(this.phpIndex);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final String simpleResourceName = getSimpleResourceName();
        final PrefixMatcher matcher = new StrictCamelHumpMatcher(resourceName.replace(simpleResourceName, "").replace("@", ""));
        final PsiFile[] files = FilenameIndex.getFilesByName(getElement().getProject(), simpleResourceName, GlobalSearchScope.allScope(getElement().getProject()));

        return Stream.of(files)
            .filter(file -> file.getVirtualFile() != null && matches(matcher, file.getVirtualFile().getPath()))
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    private String getSimpleResourceName() {
        final String[] parts = resourceName.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }

    private boolean matches(PrefixMatcher matcher, String path) {
        final int slashIndex = path.indexOf("/");
        if(slashIndex < 0) return false;
        return matcher.prefixMatches(path) || matches(matcher, path.substring(slashIndex + 1));
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return bundles.findAll().stream()
            .flatMap(bundle ->
                phpIndex.getNamespacesByName(bundle.getNamespaceName()).stream()
                    .flatMap(phpNamespace -> findFileAndResource(bundle, "", getResourcesDirectory(phpNamespace)).stream())
                    .map(fileAndResource -> LookupElementBuilder.create(fileAndResource.getResource().getName()).withIcon(fileAndResource.getFile().map(file -> file.getFileType().getIcon()).orElse(null)))
            ).toArray();
    }

    private VirtualFile getResourcesDirectory(PhpNamespace phpNamespace) {
        final PsiDirectory dir = phpNamespace.getContainingFile().getContainingDirectory();
        return VfsUtil.findRelativeFile(dir.getVirtualFile(), "Resources");
    }

    private Collection<FileAndResource> findFileAndResource(Bundle bundle, String parentDirectory, VirtualFile dir) {
        if(dir == null) return Collections.emptyList();

        final Collection<FileAndResource> files = new LinkedList<>();

        for (VirtualFile file : dir.getChildren()) {
            if(extension.equals(file.getExtension())) {
                final Resource resource = new Resource(bundle, parentDirectory + file.getName());
                files.add(new FileAndResource(PsiManager.getInstance(myElement.getProject()).findFile(file), resource));
            } else if(file.isDirectory()) {
                files.addAll(findFileAndResource(bundle, parentDirectory + file.getName() + "/", file));
            }
        }

        return files;
    }

    private static class FileAndResource {
        private final Resource resource;
        private final PsiFile file;

        private FileAndResource(PsiFile file, Resource resource) {
            this.resource = resource;
            this.file = file;
        }

        public Optional<PsiFile> getFile() {
            return Optional.ofNullable(file);
        }

        public Resource getResource() {
            return resource;
        }
    }
}
