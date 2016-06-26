package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

class RequireJsModuleReferenceProvider extends PsiReferenceProvider {

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull ProcessingContext context) {
        final String text = StringUtil.unquoteString(element.getText().trim().replace(PsiElements.IN_PROGRESS_VALUE, ""));

        final List<? extends FileReference> references = getReferences(element, text);

        return references.toArray(new PsiReference[references.size()]);
    }

    private List<? extends FileReference> getReferences(PsiElement element, String text) {
        final VirtualFile jsRootDir = getJsRootDir(element.getProject());

        if(jsRootDir == null) {
            return Collections.emptyList();
        }

        if(!text.startsWith(RequireJsFileReference.OROUI_JS)) {
            return emptyReferences(element, jsRootDir);
        }

        final String referenceFilePath = jsRootDir.getPath() + "/" + text.replace(RequireJsFileReference.OROUI_JS, "") + ".js";
        final List<FileReference> references = getFileReferences(element, jsRootDir, referenceFilePath);
        references.addAll(emptyReferences(element, jsRootDir));

        return references;
    }

    private List<? extends FileReference> emptyReferences(final PsiElement element, VirtualFile jsRootDir) {
        final String relativePath = relativePathTo(jsRootDir, element.getOriginalElement().getContainingFile().getOriginalFile().getVirtualFile().getParent());

        if(!element.getText().contains(PsiElements.IN_PROGRESS_VALUE) || relativePath == null) {
            return Collections.emptyList();
        }

        final FileReferenceSet fileReferenceSet = new FileReferenceSet(relativePath, element, 0, this, true);

        return Arrays.asList(new RequireJsFileReference(fileReferenceSet, element, "", getJsRootDir(element.getProject())));
    }

    @Nullable
    private VirtualFile getJsRootDir(Project project) {

        final PhpClass uiClass = getOroUIBundleClass(project);

        if(uiClass == null) {
            return null;
        }

        final VirtualFile file = uiClass.getContainingFile().getVirtualFile().getParent();

        return VfsUtil.findRelativeFile(file, "Resources", "public", "js");
    }

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
            return file.getPath().replace(commonAncestor.getPath()+"/", "");
        }

        return StringUtil.join(parts, "/") + "/" + file.getPath().replace(commonAncestor.getPath()+"/", "");
    }

    private PhpClass getOroUIBundleClass(Project project) {
        final PhpIndex phpIndex = PhpIndex.getInstance(project);

        final Collection<PhpClass> classes = phpIndex.getClassesByFQN("\\Oro\\Bundle\\UIBundle\\OroUIBundle");

        return classes.isEmpty() ? null : classes.iterator().next();
    }

    @NotNull
    private List<FileReference> getFileReferences(@NotNull final PsiElement element, final VirtualFile jsRootDir, final String referenceFilePath) {
        final String relativePath = relativePathTo(jsRootDir, element.getOriginalElement().getContainingFile().getOriginalFile().getVirtualFile().getParent());

        if(relativePath == null) {
            return Collections.emptyList();
        }

        final FileReferenceSet fileReferenceSet = new FileReferenceSet(relativePath, element, 0, this, true);
        final List<FileReference> references = new LinkedList<FileReference>();

        VfsUtilCore.iterateChildrenRecursively(jsRootDir, null, new ContentIterator() {
            @Override
            public boolean processFile(VirtualFile jsFile) {
                if(jsFile.getPath().equals(referenceFilePath)) {
                    references.add(new RequireJsFileReference(fileReferenceSet, element, relativePath + "/" +jsFile.getPath().replace(jsRootDir.getPath()+"/", ""), jsRootDir));
                }
                return true;
            }
        });
        return references;
    }

}
