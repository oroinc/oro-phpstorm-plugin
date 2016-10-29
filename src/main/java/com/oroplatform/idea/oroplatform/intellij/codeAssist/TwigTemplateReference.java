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
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.symfony.Bundle;
import com.oroplatform.idea.oroplatform.symfony.Bundles;
import com.oroplatform.idea.oroplatform.symfony.TwigTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Stream;

public class TwigTemplateReference extends PsiPolyVariantReferenceBase<PsiElement> {

    private final String templateName;
    private final PhpIndex phpIndex;
    private final Bundles bundles;

    public TwigTemplateReference(PsiElement psiElement, String templateName) {
        super(psiElement);
        this.templateName = templateName;
        this.phpIndex = PhpIndex.getInstance(psiElement.getProject());
        this.bundles = new Bundles(this.phpIndex);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final String simpleTemplateName = getSimpleTemplateName();
        final PrefixMatcher matcher = new StrictCamelHumpMatcher(templateName.replace(simpleTemplateName, "").replace(":", "").replace("/", ""));
        final PsiFile[] files = FilenameIndex.getFilesByName(getElement().getProject(), simpleTemplateName, GlobalSearchScope.allScope(getElement().getProject()));

        return Stream.of(files)
            .filter(file -> file.getVirtualFile() != null && matches(matcher, file.getVirtualFile().getPath()))
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    private boolean matches(PrefixMatcher matcher, String path) {
        final int slashIndex = path.indexOf("/");
        if(slashIndex < 0) return false;
        return matcher.prefixMatches(path) || matches(matcher, path.substring(slashIndex + 1));
    }

    private String getSimpleTemplateName() {
        final String[] parts = templateName.split("/|:");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return bundles.findAll().stream()
            .flatMap(bundle ->
                phpIndex.getNamespacesByName(bundle.getNamespaceName()).stream()
                    .flatMap(phpNamespace -> findTwigTemplates(bundle, "", "", getViewsDirForBundleNamespace(phpNamespace)).stream())
                    .map(twigTemplate -> LookupElementBuilder.create(twigTemplate.getName()).withIcon(Icons.TWIG))
            ).toArray();
    }

    private VirtualFile getViewsDirForBundleNamespace(PhpNamespace phpNamespace) {
        final PsiDirectory dir = phpNamespace.getContainingFile().getContainingDirectory();
        return VfsUtil.findRelativeFile(dir.getVirtualFile(), "Resources", "views");
    }

    private Collection<TwigTemplate> findTwigTemplates(Bundle bundle, String topDirectory, String prefixPath, VirtualFile dir) {
        if(dir == null) return Collections.emptyList();

        final Collection<TwigTemplate> twigFiles = new LinkedList<>();

        for (VirtualFile file : dir.getChildren()) {
            if("twig".equals(file.getExtension())) {
                twigFiles.add(new TwigTemplate(bundle, topDirectory, prefixPath + "/" + file.getName()));
            } else if(file.isDirectory()) {
                if("".equals(topDirectory)) {
                    twigFiles.addAll(findTwigTemplates(bundle, file.getName(), prefixPath, file));
                } else {
                    twigFiles.addAll(findTwigTemplates(bundle, topDirectory, prefixPath + "/" + file.getName(), file));
                }
            }
        }

        return twigFiles;
    }

}
