package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.PhpClassUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TwigTemplateReference extends PsiPolyVariantReferenceBase<PsiElement> {

    private final String templateName;
    private final PhpIndex phpIndex;

    public TwigTemplateReference(PsiElement psiElement, String templateName) {
        super(psiElement);
        this.templateName = templateName;
        this.phpIndex = PhpIndex.getInstance(psiElement.getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final String simpleTemplateName = getSimpleTemplateName();
        final PsiFile[] files = FilenameIndex.getFilesByName(getElement().getProject(), simpleTemplateName, GlobalSearchScope.allScope(getElement().getProject()));

        final List<ResolveResult> results = new LinkedList<ResolveResult>();

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
        final Collection<String> bundleNamespaceNames = getBundleNamespaceNames();

        final List<LookupElement> results = new LinkedList<LookupElement>();

        //TODO: refactor - maybe extract ResourceFile or something
        for (String namespaceName : bundleNamespaceNames) {
            for (PhpNamespace phpNamespace : phpIndex.getNamespacesByName(namespaceName)) {
                final PsiDirectory dir = phpNamespace.getContainingFile().getContainingDirectory();
                final VirtualFile views = VfsUtil.findRelativeFile(dir.getVirtualFile(), "Resources", "views");

                final Collection<String> twigFiles = getTwigFiles("", views);
                final String bundleName = PhpClassUtil.getBundleName(namespaceName);

                for (String twigFile : twigFiles) {
                    final List<String> parts = Arrays.asList(StringUtil.trimStart(twigFile, "/").split("/"));
                    final String actionPart = parts.size() == 1 ? "" : parts.get(0);
                    final List<String> otherParts = parts.size() > 1 ? parts.subList(1, parts.size()) : parts;
                    results.add(LookupElementBuilder.create(bundleName+":"+actionPart+":"+StringUtil.join(otherParts, "/")).withIcon(Icons.TWIG));
                }
            }
        }

        return results.toArray();
    }

    private Collection<String> getTwigFiles(String prefixPath, VirtualFile dir) {
        if(dir == null) return Collections.emptyList();

        final Collection<String> twigFiles = new LinkedList<String>();

        for (VirtualFile file : dir.getChildren()) {
            if("twig".equals(file.getExtension())) {
                twigFiles.add(prefixPath + "/" + file.getName());
            } else if(file.isDirectory()) {
                twigFiles.addAll(getTwigFiles(prefixPath + "/" + file.getName(), file));
            }
        }

        return twigFiles;
    }

    //TODO: almost copy & paste from PhpClassReference
    private Collection<String> getBundleNamespaceNames() {
        Collection<PhpClass> classes = phpIndex.getAllSubclasses("\\Symfony\\Component\\HttpKernel\\Bundle\\Bundle");
        Collection<String> namespaces = new HashSet<String>();

        for (PhpClass phpClass : classes) {
            namespaces.add(StringUtil.trimEnd(phpClass.getNamespaceName(), "\\"));
        }

        return namespaces;
    }
}
