package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Pair;
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WrappedFileReferenceProvider extends PsiReferenceProvider {
    private final StringWrapperProvider stringWrapperProvider;
    private final RootDirsFinder rootDirsFinder;
    private final VirtualFileFilter fileFilter;
    private final FilePathTransformer filePathTransformer;

    public WrappedFileReferenceProvider(StringWrapperProvider stringWrapperProvider, RootDirsFinder rootDirsFinder, VirtualFileFilter fileFilter) {
        this(stringWrapperProvider, rootDirsFinder, fileFilter, new NullFilePathTransformer());
    }

    public WrappedFileReferenceProvider(StringWrapperProvider stringWrapperProvider, RootDirsFinder rootDirsFinder, VirtualFileFilter fileFilter, FilePathTransformer filePathTransformer) {
        this.stringWrapperProvider = stringWrapperProvider;
        this.rootDirsFinder = rootDirsFinder;
        this.fileFilter = fileFilter;
        this.filePathTransformer = filePathTransformer;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull ProcessingContext context) {
        final Pair<PsiElement, String> processedElementAndText = PsiElements.getProcessedElementAndText(element, context);
        final String text = StringUtil.unquoteString(processedElementAndText.second.trim().replace(PsiElements.IN_PROGRESS_VALUE, ""));

        final List<? extends FileReference> references = getReferences(processedElementAndText.first, text);

        return references.toArray(new PsiReference[references.size()]);
    }

    private List<? extends FileReference> getReferences(PsiElement element, String text) {
        final List<StringWrapperAndSourceDir> wrappersAndDirs = rootDirsFinder.getRootDirs(element).stream()
            .map(rootDir -> {
                final StringWrapper stringWrapper = stringWrapperProvider.getStringWrapperFor(element, rootDir);
                return new StringWrapperAndSourceDir(rootDir, stringWrapper);
            }).collect(Collectors.toList());

        final String transformedText = filePathTransformer.referenceFilePath(element, text);

        final Stream<FileReference> references = wrappersAndDirs.stream()
            .filter(wrapperAndDir -> wrapperAndDir.stringWrapper.startWith(transformedText))
            .flatMap(wrapperAndDir -> {
                final String referenceFilePath = wrapperAndDir.sourceDir.getPath() + "/" + wrapperAndDir.stringWrapper.removePrefixAndAddSuffix(transformedText);
                return getFileReferences(element, wrapperAndDir.sourceDir, referenceFilePath, wrapperAndDir.stringWrapper).stream();
            });

        return Stream.concat(references, emptyReferences(element, wrappersAndDirs).stream())
            .collect(Collectors.toList());
    }

    private List<? extends FileReference> emptyReferences(final PsiElement element, List<StringWrapperAndSourceDir> wrappersAndDirs) {
        return wrappersAndDirs.stream()
            .flatMap(wrapperAndDir -> {
                final String relativePath = relativePathTo(wrapperAndDir.sourceDir, element.getOriginalElement().getContainingFile().getOriginalFile().getVirtualFile().getParent());

                if(!element.getText().contains(PsiElements.IN_PROGRESS_VALUE) || relativePath == null) {
                    return Stream.empty();
                }

                final FileReferenceSet fileReferenceSet = new FileReferenceSet(relativePath, element, 0, this, true);

                return Stream.of(new WrappedFileReference(wrapperAndDir.stringWrapper, fileReferenceSet, element, "", wrapperAndDir.sourceDir, fileFilter, filePathTransformer));
            })
            .collect(Collectors.toList());

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
                references.add(new WrappedFileReference(stringWrapper, fileReferenceSet, element, relativePath + file.getPath().replace(rootDir.getPath()+"/", ""), rootDir, fileFilter, filePathTransformer));
            }
            return true;
        });
        return references;
    }

    private static class WrappedFileReference extends FileReference {

        private final StringWrapper stringWrapper;
        private final VirtualFile rootDir;
        private final VirtualFileFilter fileFilter;
        private final FilePathTransformer filePathTransformer;

        WrappedFileReference(StringWrapper stringWrapper, FileReferenceSet fileReferenceSet, PsiElement element, String text, VirtualFile rootDir, VirtualFileFilter fileFilter, FilePathTransformer filePathTransformer) {
            super(fileReferenceSet, new TextRange(isQuotedAsInt(element), element.getTextLength() - isQuotedAsInt(element)), 0, text);
            this.stringWrapper = stringWrapper;
            this.rootDir = rootDir;
            this.fileFilter = fileFilter;
            this.filePathTransformer = filePathTransformer;
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
            return filePathTransformer.variantLookupString(getElement(), stringWrapper.addPrefixAndRemoveSuffix(file.getPath().replace(rootDir.getPath() + "/", "")));
        }
    }

    private static class StringWrapperAndSourceDir {
        private final VirtualFile sourceDir;
        private final StringWrapper stringWrapper;

        private StringWrapperAndSourceDir(VirtualFile sourceDir, StringWrapper stringWrapper) {
            this.sourceDir = sourceDir;
            this.stringWrapper = stringWrapper;
        }
    }

    public interface FilePathTransformer {
        String referenceFilePath(PsiElement element, String text);
        String variantLookupString(PsiElement element, String text);
    }

    public static class NullFilePathTransformer implements FilePathTransformer {
        @Override
        public String referenceFilePath(PsiElement element, String text) {
            return text;
        }

        @Override
        public String variantLookupString(PsiElement element, String text) {
            return text;
        }
    }

}
