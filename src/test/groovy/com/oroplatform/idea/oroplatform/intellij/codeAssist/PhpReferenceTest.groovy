package com.oroplatform.idea.oroplatform.intellij.codeAssist

import com.intellij.psi.PsiPolyVariantReferenceBase
import com.jetbrains.php.lang.psi.elements.PhpNamedElement


abstract class PhpReferenceTest extends CompletionTest{
    def checkPhpReference(String content, List<String> expectedReferences) {
        assertEquals(expectedReferences, getPhpReference(content))
    }

    def List<String> getPhpReference(String content) {
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
            .collect { (it.getElement() as PhpNamedElement).getName() }
            .unique()
            .toList()
    }

}
