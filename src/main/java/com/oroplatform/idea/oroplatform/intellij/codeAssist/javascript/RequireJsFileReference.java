package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

class RequireJsFileReference extends FileReference {

    static final String OROUI_JS = "oroui/js/";

    private final VirtualFile jsRootDir;

    RequireJsFileReference(FileReferenceSet fileReferenceSet, PsiElement element, String text, VirtualFile jsRootDir) {
        super(fileReferenceSet, new TextRange(1, element.getTextLength() - 1), 0, text);
        this.jsRootDir = jsRootDir;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        if(jsRootDir == null) {
            return new Object[0];
        }

        final List<LookupElement> elements = new LinkedList<LookupElement>();

        VfsUtilCore.iterateChildrenRecursively(jsRootDir, null, new ContentIterator() {
            @Override
            public boolean processFile(VirtualFile jsFile) {
                if(!jsFile.isDirectory()) {
                    elements.add(LookupElementBuilder.create(OROUI_JS+jsFile.getPath().replace(jsRootDir.getPath()+"/", "").replace(".js", "")));
                }
                return true;
            }
        });

        return elements.toArray();
    }
}
