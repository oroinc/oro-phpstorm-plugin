package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String resourceName;
    private final String extension;
    private final PhpIndex phpIndex;
    private final Function<Resource, String> resourceRenderer;
    private final List<String> pathInResources;
    private final Bundles bundles;

    public ResourceReference(PsiElement psiElement, String resourceName, String extension) {
        this(psiElement, resourceName, extension, Resource::getName, Collections.emptyList());
    }

    public ResourceReference(PsiElement psiElement, String resourceName, String extension, Function<Resource, String> resourceRenderer, List<String> pathInResources) {
        super(psiElement);
        this.resourceName = resourceName;
        this.extension = extension;

        this.phpIndex = PhpIndex.getInstance(psiElement.getProject());
        this.resourceRenderer = resourceRenderer;
        this.pathInResources = pathInResources;
        this.bundles = new Bundles(this.phpIndex);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final String simpleResourceName = getSimpleResourceName();
        final PrefixMatcher matcher = new StrictCamelHumpMatcher(resourceName.replace(simpleResourceName, "").replace("@", "").replace(":", ""));
        final PsiFile[] files = FilenameIndex.getFilesByName(getElement().getProject(), simpleResourceName, GlobalSearchScope.allScope(getElement().getProject()));

        return Stream.of(files)
            .filter(file -> file.getVirtualFile() != null && matches(matcher, file.getVirtualFile().getPath()))
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    private String getSimpleResourceName() {
        final String[] parts = resourceName.split("/|:");
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
        final String rootPath = StringUtil.trimStart(pathInResources.stream().collect(Collectors.joining("/")) + "/", "/");
        return bundles.findAll().stream()
            .flatMap(bundle ->
                phpIndex.getNamespacesByName(bundle.getNamespaceName()).stream()
                    .flatMap(phpNamespace -> findFileAndResource(bundle, rootPath, getResourcesDirectory(phpNamespace)).stream())
                    .map(fileAndResource -> LookupElementBuilder.create(resourceRenderer.apply(fileAndResource.getResource())).withIcon(fileAndResource.getFile().map(file -> file.getFileType().getIcon()).orElse(null)))
            ).toArray();
    }

    private VirtualFile getResourcesDirectory(PhpNamespace phpNamespace) {
        final PsiDirectory dir = phpNamespace.getContainingFile().getContainingDirectory();
        final List<String> path = getPath();

        return VfsUtil.findRelativeFile(dir.getVirtualFile(), path.stream().toArray(String[]::new));
    }

    @NotNull
    private List<String> getPath() {
        final List<String> path = new LinkedList<>();
        path.add("Resources");
        path.addAll(pathInResources);
        return path;
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
