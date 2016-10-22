package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ProcessingContext;
import com.oroplatform.idea.oroplatform.StringWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WrappedFileReferenceProvider extends PsiReferenceProvider {
    private final StringWrapperProvider stringWrapperProvider;
    private final RootDirFinder rootDirFinder;
    private final VirtualFileFilter fileFilter;

    public WrappedFileReferenceProvider(StringWrapperProvider stringWrapperProvider, RootDirFinder rootDirFinder) {
        this(stringWrapperProvider, rootDirFinder, VirtualFileFilter.ALL);
    }

    public WrappedFileReferenceProvider(StringWrapperProvider stringWrapperProvider, RootDirFinder rootDirFinder, VirtualFileFilter fileFilter) {
        this.stringWrapperProvider = stringWrapperProvider;
        this.rootDirFinder = rootDirFinder;
        this.fileFilter = fileFilter;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull ProcessingContext context) {
        final String text = StringUtil.unquoteString(element.getText().trim().replace(PsiElements.IN_PROGRESS_VALUE, ""));

        final List<? extends FileReference> references = getReferences(element, text);

        return references.toArray(new PsiReference[references.size()]);
    }

    private List<? extends FileReference> getReferences(PsiElement element, String text) {
       return rootDirFinder.getRootDir(element)
            .map(rootDir -> {
                final StringWrapper stringWrapper = stringWrapperProvider.getStringWrapperFor(element);

                if(!stringWrapper.startWith(text)) {
                    return emptyReferences(element, rootDir, stringWrapper);
                }

                final String referenceFilePath = rootDir.getPath() + "/" + stringWrapper.removePrefixAndAddSuffix(text);
                final List<FileReference> references = getFileReferences(element, rootDir, referenceFilePath, stringWrapper);
                references.addAll(emptyReferences(element, rootDir, stringWrapper));

                return references;
            }).orElseGet(Collections::emptyList);
    }

    private List<? extends FileReference> emptyReferences(final PsiElement element, VirtualFile rootDir, StringWrapper stringWrapper) {
        final String relativePath = relativePathTo(rootDir, element.getOriginalElement().getContainingFile().getOriginalFile().getVirtualFile().getParent());

        if(!element.getText().contains(PsiElements.IN_PROGRESS_VALUE) || relativePath == null) {
            return Collections.emptyList();
        }

        final FileReferenceSet fileReferenceSet = new FileReferenceSet(relativePath, element, 0, this, true);

        return Arrays.asList(new WrappedFileReference(stringWrapper, fileReferenceSet, element, "", rootDirFinder.getRootDir(element).orElse(null), fileFilter));
    }

    /**
     * @return Relative path to $file according to $relativeTo. "/" at the end is included if necessary
     */
    private String relativePathTo(VirtualFile file, VirtualFile relativeTo) {
        final VirtualFile commonAncestor = VfsUtil.getCommonAncestor(file, relativeTo);

        if(commonAncestor == null) return null;

        int level = 0;
        for(VirtualFile parent=relativeTo; !parent.equals(commonAncestor); parent=parent.getParent()) {
            level++;
        }

        final String[] parts = new String[level];

        for(int i=0; i<level; i++) {
            parts[i] = "..";
        }

        if (parts.length == 0) {
            final String path = StringUtil.trimStart(file.getPath().replace(commonAncestor.getPath(), ""), "/");

            return path.length() > 0 ? path + "/" : "";
        }

        return StringUtil.trimEnd(StringUtil.join(parts, "/") + "/" + StringUtil.trimStart(file.getPath().replace(commonAncestor.getPath(), ""), "/"), '/')+"/";
    }

    @NotNull
    private List<FileReference> getFileReferences(@NotNull final PsiElement element, final VirtualFile rootDir, final String referenceFilePath, final StringWrapper stringWrapper) {
        final String relativePath = relativePathTo(rootDir, element.getOriginalElement().getContainingFile().getOriginalFile().getVirtualFile().getParent());

        if(relativePath == null) {
            return Collections.emptyList();
        }

        final FileReferenceSet fileReferenceSet = new FileReferenceSet(relativePath, element, 0, this, true);
        final List<FileReference> references = new LinkedList<>();

        VfsUtilCore.iterateChildrenRecursively(rootDir, null, file -> {
            if(file.getPath().equals(referenceFilePath)) {
                references.add(new WrappedFileReference(stringWrapper, fileReferenceSet, element, relativePath + file.getPath().replace(rootDir.getPath()+"/", ""), rootDir, fileFilter));
            }
            return true;
        });
        return references;
    }

    private static class WrappedFileReference extends FileReference {

        private final StringWrapper stringWrapper;
        private final VirtualFile rootDir;
        private final VirtualFileFilter fileFilter;

        WrappedFileReference(StringWrapper stringWrapper, FileReferenceSet fileReferenceSet, PsiElement element, String text, VirtualFile rootDir, VirtualFileFilter fileFilter) {
            super(fileReferenceSet, new TextRange(isQuotedAsInt(element), element.getTextLength() - isQuotedAsInt(element)), 0, text);
            this.stringWrapper = stringWrapper;
            this.rootDir = rootDir;
            this.fileFilter = fileFilter;
        }

        private static int isQuotedAsInt(PsiElement element) {
            return StringUtil.isQuotedString(element.getText()) ? 1 : 0;
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            if(rootDir == null) {
                return new Object[0];
            }

            final List<LookupElement> elements = new LinkedList<>();

            VfsUtilCore.iterateChildrenRecursively(rootDir, null, file -> {
                if(!file.isDirectory() && fileFilter.accept(file)) {
                    final LookupElementBuilder lookupElement = LookupElementBuilder.create(getLookupString(file)).withIcon(file.getFileType().getIcon());
                    elements.add(lookupElement);
                }
                return true;
            });

            return elements.toArray();
        }

        @NotNull
        private String getLookupString(VirtualFile file) {
            return stringWrapper.addPrefixAndRemoveSuffix(file.getPath().replace(rootDir.getPath() + "/", ""));
        }
    }
}
