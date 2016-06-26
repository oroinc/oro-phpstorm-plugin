package com.oroplatform.idea.oroplatform.intellij.codeAssist

import com.intellij.psi.PsiFileSystemItem


abstract class FileReferenceTest extends CompletionTest {

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
}
