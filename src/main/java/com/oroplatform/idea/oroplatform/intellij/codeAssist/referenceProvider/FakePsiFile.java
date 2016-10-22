package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.lang.FileASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class FakePsiFile implements PsiFile {
    private final PsiDirectory psiDirectory;

    FakePsiFile(PsiDirectory psiDirectory) {
        this.psiDirectory = psiDirectory;
    }

    @NotNull
    @Override
    public VirtualFile getVirtualFile() {
        return psiDirectory.getVirtualFile();
    }

    @Override
    public PsiDirectory getContainingDirectory() {
        return psiDirectory.getParentDirectory();
    }

    @NotNull
    @Override
    public String getName() {
        return psiDirectory.getName();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return psiDirectory.getPresentation();
    }

    @Override
    public boolean processChildren(PsiElementProcessor<PsiFileSystemItem> processor) {
        return psiDirectory.processChildren(processor);
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return psiDirectory.setName(name);
    }

    @Override
    public boolean isDirectory() {
        return psiDirectory.isDirectory();
    }

    @NotNull
    @Override
    public Project getProject() throws PsiInvalidElementAccessException {
        return psiDirectory.getProject();
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return psiDirectory.getLanguage();
    }

    @Override
    public PsiManager getManager() {
        return psiDirectory.getManager();
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return psiDirectory.getChildren();
    }

    @Nullable
    @Override
    public PsiDirectory getParent() {
        return psiDirectory.getParent();
    }

    @Override
    public long getModificationStamp() {
        return 0;
    }

    @NotNull
    @Override
    public PsiFile getOriginalFile() {
        return this;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return null;
    }

    @NotNull
    @Override
    public PsiFile[] getPsiRoots() {
        return new PsiFile[0];
    }

    @NotNull
    @Override
    public FileViewProvider getViewProvider() {
        return null;
    }

    @Override
    public PsiElement getFirstChild() {
        return psiDirectory.getFirstChild();
    }

    @Override
    public PsiElement getLastChild() {
        return psiDirectory.getLastChild();
    }

    @Override
    public PsiElement getNextSibling() {
        return psiDirectory.getNextSibling();
    }

    @Override
    public PsiElement getPrevSibling() {
        return psiDirectory.getPrevSibling();
    }

    @Override
    public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
        return this;
    }

    @Override
    public TextRange getTextRange() {
        return psiDirectory.getTextRange();
    }

    @Override
    public int getStartOffsetInParent() {
        return psiDirectory.getStartOffsetInParent();
    }

    @Override
    public int getTextLength() {
        return psiDirectory.getTextLength();
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        return psiDirectory.findElementAt(offset);
    }

    @Nullable
    @Override
    public PsiReference findReferenceAt(int offset) {
        return psiDirectory.findReferenceAt(offset);
    }

    @Override
    public int getTextOffset() {
        return psiDirectory.getTextOffset();
    }

    @Override
    public String getText() {
        return psiDirectory.getText();
    }

    @NotNull
    @Override
    public char[] textToCharArray() {
        return psiDirectory.textToCharArray();
    }

    @Override
    public PsiElement getNavigationElement() {
        return psiDirectory.getNavigationElement();
    }

    @Override
    public PsiElement getOriginalElement() {
        return psiDirectory.getOriginalElement();
    }

    @Override
    public boolean textMatches(@NotNull @NonNls CharSequence text) {
        return psiDirectory.textMatches(text);
    }

    @Override
    public boolean textMatches(@NotNull PsiElement element) {
        return psiDirectory.textMatches(element);
    }

    @Override
    public boolean textContains(char c) {
        return psiDirectory.textContains(c);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        psiDirectory.accept(visitor);
    }

    @Override
    public void acceptChildren(@NotNull PsiElementVisitor visitor) {
        psiDirectory.acceptChildren(visitor);
    }

    @Override
    public PsiElement copy() {
        return psiDirectory.copy();
    }

    @Override
    public PsiElement add(@NotNull PsiElement element) throws IncorrectOperationException {
        return psiDirectory.add(element);
    }

    @Override
    public PsiElement addBefore(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException {
        return psiDirectory.addBefore(element, anchor);
    }

    @Override
    public PsiElement addAfter(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException {
        return psiDirectory.addAfter(element, anchor);
    }

    @Override
    public void checkAdd(@NotNull PsiElement element) throws IncorrectOperationException {
        psiDirectory.checkAdd(element);
    }

    @Override
    public PsiElement addRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        return psiDirectory.addRange(first, last);
    }

    @Override
    public PsiElement addRangeBefore(@NotNull PsiElement first, @NotNull PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return psiDirectory.addRangeBefore(first, last, anchor);
    }

    @Override
    public PsiElement addRangeAfter(PsiElement first, PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return psiDirectory.addRangeAfter(first, last, anchor);
    }

    @Override
    public void delete() throws IncorrectOperationException {
        psiDirectory.delete();
    }

    @Override
    public void checkDelete() throws IncorrectOperationException {
        psiDirectory.checkDelete();
    }

    @Override
    public void deleteChildRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        psiDirectory.deleteChildRange(first, last);
    }

    @Override
    public PsiElement replace(@NotNull PsiElement newElement) throws IncorrectOperationException {
        return psiDirectory.replace(newElement);
    }

    @Override
    public boolean isValid() {
        return psiDirectory.isValid();
    }

    @Override
    public boolean isWritable() {
        return psiDirectory.isWritable();
    }

    @Nullable
    @Override
    public PsiReference getReference() {
        return psiDirectory.getReference();
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return psiDirectory.getReferences();
    }

    @Nullable
    @Override
    public <T> T getCopyableUserData(Key<T> key) {
        return psiDirectory.getCopyableUserData(key);
    }

    @Override
    public <T> void putCopyableUserData(Key<T> key, @Nullable T value) {
        psiDirectory.putCopyableUserData(key, value);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, @Nullable PsiElement lastParent, @NotNull PsiElement place) {
        return psiDirectory.processDeclarations(processor, state, lastParent, place);
    }

    @Nullable
    @Override
    public PsiElement getContext() {
        return this;
    }

    @Override
    public boolean isPhysical() {
        return psiDirectory.isPhysical();
    }

    @NotNull
    @Override
    public GlobalSearchScope getResolveScope() {
        return psiDirectory.getResolveScope();
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return psiDirectory.getUseScope();
    }

    @Override
    public FileASTNode getNode() {
        return null;
    }

    @Override
    public void subtreeChanged() {

    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
        return psiDirectory.isEquivalentTo(another);
    }

    @Override
    public void navigate(boolean requestFocus) {
        psiDirectory.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return psiDirectory.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return psiDirectory.canNavigateToSource();
    }

    @Override
    public void checkSetName(String name) throws IncorrectOperationException {
        psiDirectory.checkSetName(name);
    }

    @Override
    public Icon getIcon(@IconFlags int flags) {
        return psiDirectory.getIcon(flags);
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return psiDirectory.getUserData(key);
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
        psiDirectory.putUserData(key, value);
    }
}
