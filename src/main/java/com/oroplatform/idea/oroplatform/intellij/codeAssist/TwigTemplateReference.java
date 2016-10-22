package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
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

import java.util.*;

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
        final PsiFile[] files = FilenameIndex.getFilesByName(getElement().getProject(), simpleTemplateName, GlobalSearchScope.allScope(getElement().getProject()));

        final List<ResolveResult> results = new LinkedList<>();

        final PrefixMatcher matcher = new StrictCamelHumpMatcher(templateName.replace(simpleTemplateName, "").replace(":", "").replace("/", ""));

        for (PsiFile file : files) {
            if(file.getVirtualFile() != null && matches(matcher, file.getVirtualFile().getPath())) {
                results.add(new PsiElementResolveResult(file));
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
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
        final List<LookupElement> results = new LinkedList<>();

        for (Bundle bundle : bundles.findAll()) {
            for (PhpNamespace phpNamespace : phpIndex.getNamespacesByName(bundle.getNamespaceName())) {
                final PsiDirectory dir = phpNamespace.getContainingFile().getContainingDirectory();
                final VirtualFile views = VfsUtil.findRelativeFile(dir.getVirtualFile(), "Resources", "views");

                final Collection<TwigTemplate> twigTemplates = findTwigTemplates(bundle, "", "", views);

                for (TwigTemplate twigTemplate : twigTemplates) {
                    results.add(LookupElementBuilder.create(twigTemplate.getName()).withIcon(Icons.TWIG));
                }
            }
        }

        return results.toArray();
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
