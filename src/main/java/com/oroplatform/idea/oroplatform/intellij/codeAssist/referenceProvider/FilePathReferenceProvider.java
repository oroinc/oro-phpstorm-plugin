package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FilePathReferenceProvider extends PsiReferenceProvider {
    private final RelativeDirectoryResolver relativeToAppDir;
    private final int allowedDepth;

    public FilePathReferenceProvider(RelativeDirectoryResolver relativeDirectoryResolver, int allowedDepth) {
        this.relativeToAppDir = relativeDirectoryResolver;
        this.allowedDepth = allowedDepth;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return (relativeToAppDir == null ? new FileReferenceSet(element) : new RelativeFileReferenceSet(element, relativeToAppDir, allowedDepth))
            .getAllReferences();
    }

    private static class RelativeFileReferenceSet extends FileReferenceSet {
        private final RelativeDirectoryResolver dirResolver;
        private final int allowedDepth;

        RelativeFileReferenceSet(PsiElement element, RelativeDirectoryResolver dirResolver, int allowedDepth) {
            super(ElementManipulators.getValueTextRange(element).substring(element.getText()), element,
                ElementManipulators.getValueTextRange(element).getStartOffset(), null, true, true, null,
                /* don't reparse, because allowedDepth is not set yet */ false);
            this.dirResolver = dirResolver;
            this.allowedDepth = allowedDepth;
            reparse();
        }

        @Nullable
        @Override
        protected PsiFile getContainingFile() {
            final PsiDirectory dir = dirResolver.resolve(getElement());

            if(dir != null && dir.getFiles().length > 0) {
                return dir.getFiles()[0];
            } else if(dir != null && dir.getSubdirectories().length > 0) {
                return new FakePsiFile(dir.getSubdirectories()[0]);
            }

            return super.getContainingFile();
        }

        @Override
        protected List<FileReference> reparse(String str, int startInElement) {
            final List<FileReference> references = super.reparse(str, startInElement);

            return allowedDepth >= references.size() ? references : references.subList(0, allowedDepth);
        }
    }
}
