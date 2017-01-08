package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.oroplatform.idea.oroplatform.symfony.Bundle;
import com.oroplatform.idea.oroplatform.symfony.Bundles;
import com.oroplatform.idea.oroplatform.symfony.Resource;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

public class ResourceReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String resourceName;
    private final String extension;
    private final PhpIndex phpIndex;
    private final Function<Resource, String> resourceRenderer;
    private final List<String> pathInResources;
    private final Bundles bundles;

    public ResourceReference(PsiElement element, String resourceName, String extension) {
        this(element, resourceName, extension, Resource::getName, Collections.emptyList());
    }

    public ResourceReference(PsiElement element, String resourceName, String extension, Function<Resource, String> resourceRenderer, List<String> pathInResources) {
        super(element);
        this.resourceName = resourceName;
        this.extension = extension;

        this.phpIndex = PhpIndex.getInstance(element.getProject());
        this.resourceRenderer = resourceRenderer;
        this.pathInResources = pathInResources;
        this.bundles = new Bundles(this.phpIndex);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final String[] parts = StringUtil.trimStart(resourceName, "@").split("/|:");

        return bundles.findAll().stream()
            .filter(bundle -> parts.length > 1 && parts[0].equals(bundle.getName()))
            .flatMap(bundle -> phpIndex.getNamespacesByName(bundle.getNamespaceName()).stream())
            .flatMap(phpNamespace -> toStream(VfsUtil.findRelativeFile(phpNamespace.getContainingFile().getContainingDirectory().getVirtualFile(), getPath())))
            .distinct()
            .flatMap(resourceDir -> {
                final String resourcePath = resourceName.replaceFirst(".+?(:|/)(Resources/)?", "");
                return toStream(VfsUtil.findRelativeFile(resourceDir, resourcePath.split(":|/")));
            })
            .flatMap(file -> toStream(PsiManager.getInstance(myElement.getProject()).findFile(file)))
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
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

        return VfsUtil.findRelativeFile(dir.getVirtualFile(), getPath());
    }

    @NotNull
    private String[] getPath() {
        final List<String> path = new LinkedList<>();
        path.add("Resources");
        path.addAll(pathInResources);

        return path.stream().toArray(String[]::new);
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
