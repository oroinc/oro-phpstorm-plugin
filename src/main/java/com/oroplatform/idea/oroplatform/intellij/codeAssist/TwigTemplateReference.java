package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
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
        return new ResolveResult[0];
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
                    results.add(LookupElementBuilder.create(bundleName+":"+actionPart+":"+StringUtil.join(otherParts, "/")));
                }
            }
        }

        return results.toArray();
    }

    private Collection<String> getTwigFiles(String prefixPath, VirtualFile dir) {
        if(dir == null) return Collections.emptyList();

        final Collection<String> twigFiles = new LinkedList<String>();

        for (VirtualFile xxx : dir.getChildren()) {
            if("twig".equals(xxx.getExtension())) {
                twigFiles.add(prefixPath + "/" + xxx.getName());
            } else if(xxx.isDirectory()) {
                twigFiles.addAll(getTwigFiles(prefixPath + "/" + xxx.getName(), xxx));
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
