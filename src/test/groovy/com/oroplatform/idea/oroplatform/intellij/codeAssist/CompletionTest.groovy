package com.oroplatform.idea.oroplatform.intellij.codeAssist

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiPolyVariantReferenceBase

abstract class CompletionTest extends TestCase {

    def completion(String contents, String expected) {
        completion(fileName(), contents, expected)
    }

    @Override
    protected boolean isWriteActionRequired() {
        return false
    }

    def completion(String fileName, String contents, String expected) {
        configureByText(fileName, contents)
        def elements = myFixture.completeBasic()

        if(elements != null) {
            myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
        }

        myFixture.checkResult(expected.replace("\r", ""))
    }

    def suggestions(String contents, Collection<String> expectedSuggestions, Collection<String> unexpectedSuggestions= []) {
        suggestions(fileName(), contents, expectedSuggestions, unexpectedSuggestions)
    }

    def suggestions(String fileName, String contents, Collection<String> expectedSuggestions, Collection<String> unexpectedSuggestions= []) {
        configureByText(fileName, contents)
        myFixture.completeBasic()

        def lookupElements = myFixture.getLookupElementStrings()

        assertNotNull(lookupElements)
        assertContainsElements(lookupElements, expectedSuggestions.collect { it.toString() })
        assertDoesntContain(lookupElements, unexpectedSuggestions.collect { it.toString() })
    }

    private def String[] getResolvedFileReferences(String fileName, String contents) {
        configureByText(fileName, contents)

        def element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent()

        element.getReferences()
            .collect { it.resolve() }
            .flatten()
            .findAll { it instanceof PsiFileSystemItem }
            .collect { (PsiFileSystemItem) it }
            .collect { it.getVirtualFile().getCanonicalPath() }
    }

    protected def void checkFileReferences(String fileName, String content, List<String> expectedReferences, List<String> unexpectedReferences = []) {
        def files = getResolvedFileReferences(fileName, content)

        expectedReferences.forEach { ref ->
            assertTrue("$files does not contain '$ref'", files.findAll { it.contains(ref) }.size() > 0)
        }

        unexpectedReferences.forEach { ref ->
            assertTrue("$files contains '$ref'", files.findAll { it.contains(ref) }.isEmpty())
        }
    }

    protected def void checkFileReferences(String content, List<String> expectedReferences, List<String> unexpectedReferences = []) {
        checkFileReferences(fileName(), content, expectedReferences, unexpectedReferences)
    }

    def checkReference(String content, List<String> expectedReferences) {
        assertEquals(expectedReferences, getReferences(content))
    }

    def List<String> getReferences(String content) {
        configureByText(content)

        myFixture.getProject().getBaseDir().refresh(false, true)

        def element = myFixture.getFile().findElementAt(myFixture.getCaretOffset())
        def elements = [element, element.getParent(), element.getParent().getParent()]

        elements.collect { it.getReferences() }
            .flatten()
            .findAll { it instanceof PsiPolyVariantReferenceBase }
            .collect {  it as PsiPolyVariantReferenceBase }
            .collect { it.multiResolve(false) }
            .flatten()
            .collect { if(it.getElement() instanceof PsiNamedElement) it.getElement().getName() else it.getElement().getText() }
            .unique()
            .toList()
    }
}
