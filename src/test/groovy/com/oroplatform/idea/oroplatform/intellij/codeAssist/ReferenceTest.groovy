package com.oroplatform.idea.oroplatform.intellij.codeAssist

import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiPolyVariantReferenceBase

abstract class ReferenceTest extends CompletionTest {

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
            .collect { (it.getElement() as PsiNamedElement).getName() }
            .unique()
            .toList()
    }
}
